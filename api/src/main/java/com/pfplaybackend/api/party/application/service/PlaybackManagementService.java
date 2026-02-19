package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.party.application.aspect.context.PartyContext;
import com.pfplaybackend.api.party.application.dto.base.PartyroomDataDto;
import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomDto;
import com.pfplaybackend.api.party.application.dto.playback.PlaybackDto;
import com.pfplaybackend.api.party.application.peer.UserActivityPeerService;
import com.pfplaybackend.api.party.application.service.task.ExpirationTaskScheduler;
import com.pfplaybackend.api.party.domain.entity.converter.PartyroomConverter;
import com.pfplaybackend.api.party.domain.entity.converter.PlaybackConverter;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.entity.data.PlaybackData;
import com.pfplaybackend.api.party.domain.entity.domainmodel.Dj;
import com.pfplaybackend.api.party.domain.entity.domainmodel.Crew;
import com.pfplaybackend.api.party.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.party.domain.entity.domainmodel.Playback;
import com.pfplaybackend.api.party.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.domain.service.CrewDomainService;
import com.pfplaybackend.api.party.domain.service.DjDomainService;
import com.pfplaybackend.api.party.domain.service.PlaybackDomainService;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.value.PlaybackId;
import com.pfplaybackend.api.common.config.redis.RedisMessagePublisher;
import com.pfplaybackend.api.party.interfaces.listener.redis.message.DjQueueChangeMessage;
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
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PlaybackManagementService {

    private final PlaybackRepository playbackRepository;
    private final PlaybackConverter playbackConverter;
    private final PlaybackDomainService playbackDomainService;
    private final DjDomainService djDomainService;
    private final PlaybackInfoService playbackInfoService;
    private final PartyroomInfoService partyroomInfoService;
    private final UserActivityPeerService userActivityService;
    private final RedisMessagePublisher messagePublisher;
    private final PartyroomRepository partyroomRepository;
    private final PartyroomConverter partyroomConverter;
    private final ExpirationTaskScheduler scheduleService;
    private final CrewDomainService crewDomainService;

    private void scheduleTask(Playback playback) {
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
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        ActivePartyroomDto activePartyroomDto = partyroomRepository.getActivePartyroomByUserId(partyContext.getUserId()).orElseThrow();
        PartyroomData partyroomData = partyroomRepository.findById(activePartyroomDto.getId()).orElseThrow();
        Partyroom partyroom = partyroomConverter.toDomain(partyroomData);
        if(crewDomainService.isBelowManagerGrade(partyroom, partyContext.getUserId())) throw ExceptionCreator.create(GradeException.MANAGER_GRADE_REQUIRED);
        cancelTask(partyroomId);
        tryProceed(partyroomId);
    }

    @Transactional
    public void skipBySystem(PartyroomId partyroomId) {
        cancelTask(partyroomId);
        tryProceed(partyroomId);
    }

    private void tryProceed(PartyroomId partyroomId) {
        Optional<PartyroomDataDto> optional = partyroomRepository.findPartyroomDto(partyroomId);
        if(optional.isEmpty()) throw ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM);
        PartyroomData partyroomData = partyroomConverter.toEntity(optional.get());
        Partyroom partyroom = partyroomConverter.toDomain(partyroomData);

        // FIXME Remove DjDomainService in Here!!!
        if(djDomainService.isExistDj(partyroom)) {
            start(partyroom);
        }else{
            updatePlaybackDeactivation(partyroom);
        }
    }


    public void start(Partyroom partyroom) {
        int maxAttempts = (int) partyroom.getDjSet().stream().filter(Dj::isQueued).count();
        doStart(partyroom, maxAttempts);
    }

    private void doStart(Partyroom partyroom, int remainingAttempts) {
        // FIXME All Dj 'orderNumber' bulk update
        Partyroom updatedPartyroom = rotateDjQueue(partyroom);
        Dj nextDj = updatedPartyroom.getDjSet().stream().min(Comparator.comparingInt(Dj::getOrderNumber)).orElseThrow();
        Crew djCrew = updatedPartyroom.getCrewSet().stream().filter(crew -> crew.getUserId().equals(nextDj.getUserId())).toList().get(0);
        Playback nextPlayback = playbackInfoService.getNextPlaybackInPlaylist(updatedPartyroom.getPartyroomId(), nextDj);

        if (exceedsPlaybackTimeLimit(updatedPartyroom, nextPlayback)) {
            if (remainingAttempts <= 1) {
                updatePlaybackDeactivation(updatedPartyroom);
                return;
            }
            Optional<PartyroomDataDto> optional = partyroomRepository.findPartyroomDto(updatedPartyroom.getPartyroomId());
            if (optional.isEmpty()) throw ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM);
            Partyroom reloaded = partyroomConverter.toDomain(partyroomConverter.toEntity(optional.get()));
            if (djDomainService.isExistDj(reloaded)) {
                doStart(reloaded, remainingAttempts - 1);
            } else {
                updatePlaybackDeactivation(reloaded);
            }
            return;
        }

        PlaybackData playbackData = playbackRepository.save(playbackConverter.toData(nextPlayback));
        // Update 'CurrentPlaybackId'
        partyroomRepository.save(partyroomConverter.toData(partyroom.updatePlaybackId(new PlaybackId(playbackData.getId()))));
        // Schedule Task to wait for playback time
        scheduleTask(nextPlayback);
        // Propagation Websocket Event
        publishPlaybackChangedEvent(updatedPartyroom.getPartyroomId(), djCrew.getId(), playbackData);
        publishDjQueueChangeEvent(updatedPartyroom);
    }

    private boolean exceedsPlaybackTimeLimit(Partyroom partyroom, Playback playback) {
        int limitMinutes = partyroom.getPlaybackTimeLimit();
        if (limitMinutes <= 0) return false;
        long durationSeconds = playbackDomainService.convertToSeconds(playback.getDuration());
        return durationSeconds > limitMinutes * 60L;
    }

    // FIXME CrewId
    private void publishPlaybackChangedEvent(PartyroomId partyroomId, long crewId, PlaybackData playbackData) {
        messagePublisher.publish(MessageTopic.PLAYBACK_START,
                new PlaybackStartMessage(partyroomId, MessageTopic.PLAYBACK_START, crewId,
                        new PlaybackDto(playbackData.getId(), playbackData.getLinkId(), playbackData.getName(), playbackData.getDuration(), playbackData.getThumbnailImage(), playbackData.getEndTime())));
    }

    private void updatePlaybackDeactivation(Partyroom partyroom) {
        partyroomRepository.save(partyroomConverter.toData(partyroom.applyDeactivation()));
        messagePublisher.publish(MessageTopic.PARTYROOM_DEACTIVATION, new PartyroomDeactivationMessage(partyroom.getPartyroomId(), MessageTopic.PARTYROOM_DEACTIVATION));
    }

    private void publishDjQueueChangeEvent(Partyroom partyroom) {
        messagePublisher.publish(MessageTopic.DJ_QUEUE_CHANGE,
                DjQueueChangeMessage.create(
                        partyroom.getPartyroomId(),
                        partyroomInfoService.getDjs(partyroom)
                )
        );
    }

    private Partyroom rotateDjQueue(Partyroom partyroom) {
        PartyroomData partyroomData = partyroomRepository.save(partyroomConverter.toData(partyroom.rotateDjs()));
        return partyroomConverter.toDomain(partyroomData);
    }
}