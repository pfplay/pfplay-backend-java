package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomDto;
import com.pfplaybackend.api.party.application.dto.playback.PlaybackDto;
import com.pfplaybackend.api.party.application.peer.UserActivityPeerService;
import com.pfplaybackend.api.party.application.service.task.ExpirationTaskScheduler;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.entity.data.DjData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.entity.data.PlaybackData;
import com.pfplaybackend.api.party.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.domain.service.CrewDomainService;
import com.pfplaybackend.api.party.domain.service.DjDomainService;
import com.pfplaybackend.api.party.domain.service.PlaybackDomainService;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.value.PlaybackId;
import com.pfplaybackend.api.common.config.redis.RedisMessagePublisher;
import com.pfplaybackend.api.party.interfaces.listener.redis.message.PartyroomDeactivationMessage;
import com.pfplaybackend.api.party.interfaces.listener.redis.message.PlaybackDurationWaitMessage;
import com.pfplaybackend.api.party.interfaces.listener.redis.message.PlaybackStartMessage;
import com.pfplaybackend.api.party.domain.exception.GradeException;
import com.pfplaybackend.api.party.domain.exception.PartyroomException;
import com.pfplaybackend.api.party.infrastructure.repository.PartyroomRepository;
import com.pfplaybackend.api.party.infrastructure.repository.PlaybackRepository;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PlaybackManagementService {

    private final PlaybackRepository playbackRepository;
    private final PlaybackDomainService playbackDomainService;
    private final DjDomainService djDomainService;
    private final PlaybackInfoService playbackInfoService;
    private final UserActivityPeerService userActivityService;
    private final RedisMessagePublisher messagePublisher;
    private final PartyroomRepository partyroomRepository;
    private final ExpirationTaskScheduler scheduleService;
    private final CrewDomainService crewDomainService;

    private void scheduleTask(PlaybackData playback) {
        long seconds = playbackDomainService.convertToSeconds(playback.getDuration());
        PartyroomId partyroomId = playback.getPartyroomId();
        UserId userId = playback.getUserId();
        PlaybackDurationWaitMessage playbackDurationWaitMessage = new PlaybackDurationWaitMessage(partyroomId, userId);
        scheduleService.setKeyWithExpiration(String.valueOf(partyroomId.getId()), playbackDurationWaitMessage, seconds, TimeUnit.SECONDS);
    }

    private void cancelTask(PartyroomId partyroomId) {
        scheduleService.deleteKey(String.valueOf(partyroomId.getId()));
    }

    @Transactional
    public void complete(PartyroomId partyroomId, UserId userId) {
        tryProceed(partyroomId);
        userActivityService.updateDjPointScore(userId, 1);
    }

    @Transactional
    public void skipByManager(PartyroomId partyroomId) {
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
        ActivePartyroomDto activePartyroomDto = partyroomRepository.getActivePartyroomByUserId(authContext.getUserId()).orElseThrow();
        PartyroomData partyroom = partyroomRepository.findById(activePartyroomDto.getId()).orElseThrow();
        if(crewDomainService.isBelowManagerGrade(partyroom, authContext.getUserId())) throw ExceptionCreator.create(GradeException.MANAGER_GRADE_REQUIRED);
        cancelTask(partyroomId);
        tryProceed(partyroomId);
    }

    @Transactional
    public void skipBySystem(PartyroomId partyroomId) {
        cancelTask(partyroomId);
        tryProceed(partyroomId);
    }

    private void tryProceed(PartyroomId partyroomId) {
        PartyroomData partyroom = partyroomRepository.findById(partyroomId.getId())
                .orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));

        if(djDomainService.isExistDj(partyroom)) {
            start(partyroom);
        }else{
            updatePlaybackDeactivation(partyroom);
        }
    }

    public void start(PartyroomData partyroom) {
        int maxAttempts = (int) partyroom.getDjDataSet().stream().filter(DjData::isQueued).count();
        doStart(partyroom, maxAttempts);
    }

    private void doStart(PartyroomData partyroom, int remainingAttempts) {
        PartyroomData updatedPartyroom = rotateDjQueue(partyroom);
        DjData nextDj = updatedPartyroom.getDjDataSet().stream().min(Comparator.comparingInt(DjData::getOrderNumber)).orElseThrow();
        CrewData djCrew = updatedPartyroom.getCrewDataSet().stream().filter(crew -> crew.getUserId().equals(nextDj.getUserId())).toList().get(0);
        PlaybackData nextPlayback = playbackInfoService.getNextPlaybackInPlaylist(updatedPartyroom.getPartyroomId(), nextDj);

        if (exceedsPlaybackTimeLimit(updatedPartyroom, nextPlayback)) {
            if (remainingAttempts <= 1) {
                updatePlaybackDeactivation(updatedPartyroom);
                return;
            }
            PartyroomData reloaded = partyroomRepository.findById(updatedPartyroom.getPartyroomId().getId())
                    .orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));
            if (djDomainService.isExistDj(reloaded)) {
                doStart(reloaded, remainingAttempts - 1);
            } else {
                updatePlaybackDeactivation(reloaded);
            }
            return;
        }

        PlaybackData playbackData = playbackRepository.save(nextPlayback);
        // Update 'CurrentPlaybackId'
        partyroom.updatePlaybackId(new PlaybackId(playbackData.getId()));
        partyroomRepository.save(partyroom);
        // Schedule Task to wait for playback time
        scheduleTask(nextPlayback);
        // Propagation Websocket Event
        publishPlaybackChangedEvent(updatedPartyroom.getPartyroomId(), djCrew.getId(), playbackData);
    }

    private boolean exceedsPlaybackTimeLimit(PartyroomData partyroom, PlaybackData playback) {
        int limitSeconds = partyroom.getPlaybackTimeLimit();
        if (limitSeconds <= 0) return false;
        long durationSeconds = playbackDomainService.convertToSeconds(playback.getDuration());
        return durationSeconds > limitSeconds;
    }

    private void publishPlaybackChangedEvent(PartyroomId partyroomId, long crewId, PlaybackData playbackData) {
        messagePublisher.publish(MessageTopic.PLAYBACK_START,
                new PlaybackStartMessage(partyroomId, MessageTopic.PLAYBACK_START, crewId,
                        new PlaybackDto(playbackData.getId(), playbackData.getLinkId(), playbackData.getName(), playbackData.getDuration(), playbackData.getThumbnailImage(), playbackData.getEndTime())));
    }

    private void updatePlaybackDeactivation(PartyroomData partyroom) {
        partyroom.applyDeactivation();
        partyroomRepository.save(partyroom);
        messagePublisher.publish(MessageTopic.PARTYROOM_DEACTIVATION, new PartyroomDeactivationMessage(partyroom.getPartyroomId(), MessageTopic.PARTYROOM_DEACTIVATION));
    }

    private PartyroomData rotateDjQueue(PartyroomData partyroom) {
        partyroom.rotateDjs();
        return partyroomRepository.save(partyroom);
    }
}
