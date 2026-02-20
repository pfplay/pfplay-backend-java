package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomDto;
import com.pfplaybackend.api.party.application.dto.playback.PlaybackDto;
import com.pfplaybackend.api.party.application.port.out.UserActivityPort;
import com.pfplaybackend.api.party.application.service.task.ExpirationTaskScheduler;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.entity.data.DjData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.entity.data.PlaybackData;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.event.DjQueueChangedEvent;
import com.pfplaybackend.api.party.domain.event.PlaybackDeactivatedEvent;
import com.pfplaybackend.api.party.domain.event.PlaybackStartedEvent;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.value.PlaybackId;
import com.pfplaybackend.api.party.adapter.out.persistence.CrewRepository;
import com.pfplaybackend.api.party.adapter.out.persistence.DjRepository;
import com.pfplaybackend.api.party.adapter.in.listener.message.PlaybackDurationWaitMessage;
import com.pfplaybackend.api.party.domain.exception.GradeException;
import com.pfplaybackend.api.party.domain.exception.PartyroomException;
import com.pfplaybackend.api.party.domain.service.PartyroomAggregateService;
import com.pfplaybackend.api.party.adapter.out.persistence.PartyroomRepository;
import com.pfplaybackend.api.party.adapter.out.persistence.PlaybackRepository;
import com.pfplaybackend.api.common.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PlaybackManagementService {

    private final PlaybackRepository playbackRepository;
    private final PlaybackInfoService playbackInfoService;
    private final UserActivityPort userActivityPort;
    private final ApplicationEventPublisher eventPublisher;
    private final PartyroomRepository partyroomRepository;
    private final CrewRepository crewRepository;
    private final DjRepository djRepository;
    private final ExpirationTaskScheduler scheduleService;
    private final PartyroomAggregateService partyroomAggregateService;

    private void scheduleTask(PlaybackData playback) {
        long seconds = playback.getDuration().toSeconds();
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
        userActivityPort.updateDjPointScore(userId, 1);
    }

    @Transactional
    public void skipByManager(PartyroomId partyroomId) {
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
        ActivePartyroomDto activePartyroomDto = partyroomRepository.getActivePartyroomByUserId(authContext.getUserId()).orElseThrow();
        CrewData adjusterCrew = crewRepository.findByPartyroomDataIdAndUserId(activePartyroomDto.getId(), authContext.getUserId()).orElseThrow();
        if (adjusterCrew.isBelowGrade(GradeType.MODERATOR)) throw ExceptionCreator.create(GradeException.MANAGER_GRADE_REQUIRED);
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

        if(partyroomAggregateService.hasQueuedDjs(partyroomId.getId())) {
            start(partyroom);
        }else{
            deactivateAndNotify(partyroom);
        }
    }

    public void start(PartyroomData partyroom) {
        List<DjData> queuedDjs = djRepository.findByPartyroomDataIdAndIsQueuedTrueOrderByOrderNumberAsc(partyroom.getId());
        int maxAttempts = queuedDjs.size();
        doStart(partyroom, maxAttempts);
    }

    private void doStart(PartyroomData partyroom, int remainingAttempts) {
        partyroomAggregateService.rotateDjQueue(partyroom.getId());

        List<DjData> queuedDjs = djRepository.findByPartyroomDataIdAndIsQueuedTrueOrderByOrderNumberAsc(partyroom.getId());
        DjData nextDj = queuedDjs.stream().findFirst().orElseThrow();
        CrewData djCrew = crewRepository.findByPartyroomDataIdAndUserId(partyroom.getId(), nextDj.getUserId())
                .orElseThrow();
        PlaybackData nextPlayback = playbackInfoService.getNextPlaybackInPlaylist(partyroom.getPartyroomId(), nextDj);

        if (partyroom.getPlaybackTimeLimit().exceedsDuration(nextPlayback.getDuration())) {
            if (remainingAttempts <= 1) {
                deactivateAndNotify(partyroom);
                return;
            }
            PartyroomData reloaded = partyroomRepository.findById(partyroom.getPartyroomId().getId())
                    .orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));
            if (partyroomAggregateService.hasQueuedDjs(reloaded.getId())) {
                doStart(reloaded, remainingAttempts - 1);
            } else {
                deactivateAndNotify(reloaded);
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
        eventPublisher.publishEvent(new PlaybackStartedEvent(
                partyroom.getPartyroomId(), djCrew.getId(),
                new PlaybackDto(playbackData.getId(), playbackData.getLinkId(), playbackData.getName(),
                        playbackData.getDuration().toDisplayString(), playbackData.getThumbnailImage(), playbackData.getEndTime())));
        eventPublisher.publishEvent(new DjQueueChangedEvent(partyroom.getPartyroomId()));
    }

    private void deactivateAndNotify(PartyroomData partyroom) {
        partyroomAggregateService.deactivatePlayback(partyroom);
        eventPublisher.publishEvent(new PlaybackDeactivatedEvent(partyroom.getPartyroomId()));
    }
}
