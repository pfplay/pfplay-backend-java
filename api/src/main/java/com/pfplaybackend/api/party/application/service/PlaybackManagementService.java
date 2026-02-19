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
import com.pfplaybackend.api.party.infrastructure.repository.CrewRepository;
import com.pfplaybackend.api.party.infrastructure.repository.DjRepository;
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

import java.util.List;
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
    private final CrewRepository crewRepository;
    private final DjRepository djRepository;
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
        if(crewDomainService.isBelowManagerGrade(activePartyroomDto.getId(), authContext.getUserId())) throw ExceptionCreator.create(GradeException.MANAGER_GRADE_REQUIRED);
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

        if(djDomainService.isExistDj(partyroomId.getId())) {
            start(partyroom);
        }else{
            updatePlaybackDeactivation(partyroom);
        }
    }

    public void start(PartyroomData partyroom) {
        List<DjData> queuedDjs = djRepository.findByPartyroomDataIdAndIsQueuedTrueOrderByOrderNumberAsc(partyroom.getId());
        int maxAttempts = queuedDjs.size();
        doStart(partyroom, maxAttempts);
    }

    private void doStart(PartyroomData partyroom, int remainingAttempts) {
        rotateDjQueue(partyroom);

        List<DjData> queuedDjs = djRepository.findByPartyroomDataIdAndIsQueuedTrueOrderByOrderNumberAsc(partyroom.getId());
        DjData nextDj = queuedDjs.stream().findFirst().orElseThrow();
        CrewData djCrew = crewRepository.findByPartyroomDataIdAndUserId(partyroom.getId(), nextDj.getUserId())
                .orElseThrow();
        PlaybackData nextPlayback = playbackInfoService.getNextPlaybackInPlaylist(partyroom.getPartyroomId(), nextDj);

        if (exceedsPlaybackTimeLimit(partyroom, nextPlayback)) {
            if (remainingAttempts <= 1) {
                updatePlaybackDeactivation(partyroom);
                return;
            }
            PartyroomData reloaded = partyroomRepository.findById(partyroom.getPartyroomId().getId())
                    .orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));
            if (djDomainService.isExistDj(reloaded.getId())) {
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
        publishPlaybackChangedEvent(partyroom.getPartyroomId(), djCrew.getId(), playbackData);
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
        // Bulk dequeue all DJs
        List<DjData> queuedDjs = djRepository.findByPartyroomDataIdAndIsQueuedTrueOrderByOrderNumberAsc(partyroom.getId());
        queuedDjs.forEach(DjData::applyDequeued);
        djRepository.saveAll(queuedDjs);
        messagePublisher.publish(MessageTopic.PARTYROOM_DEACTIVATION, new PartyroomDeactivationMessage(partyroom.getPartyroomId(), MessageTopic.PARTYROOM_DEACTIVATION));
    }

    private void rotateDjQueue(PartyroomData partyroom) {
        List<DjData> queuedDjs = djRepository.findByPartyroomDataIdAndIsQueuedTrueOrderByOrderNumberAsc(partyroom.getId());
        int totalElements = queuedDjs.size();
        queuedDjs.forEach(dj -> {
            if (dj.getOrderNumber() == 1) {
                dj.updateOrderNumber(totalElements);
            } else {
                dj.updateOrderNumber(dj.getOrderNumber() - 1);
            }
        });
        djRepository.saveAll(queuedDjs);
    }
}
