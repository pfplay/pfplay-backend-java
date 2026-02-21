package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomDto;
import com.pfplaybackend.api.party.domain.value.PlaybackSnapshot;
import com.pfplaybackend.api.party.application.port.out.UserActivityPort;
import com.pfplaybackend.api.party.application.service.task.ExpirationTaskScheduler;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.entity.data.DjData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomPlaybackData;
import com.pfplaybackend.api.party.domain.entity.data.PlaybackAggregationData;
import com.pfplaybackend.api.party.domain.entity.data.PlaybackData;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.event.DjQueueChangedEvent;
import com.pfplaybackend.api.party.domain.event.PlaybackStartedEvent;
import com.pfplaybackend.api.party.domain.port.PartyroomAggregatePort;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.value.PlaybackId;
import com.pfplaybackend.api.party.adapter.out.persistence.PlaybackAggregationRepository;
import com.pfplaybackend.api.party.application.dto.playback.PlaybackDurationWaitDto;
import com.pfplaybackend.api.party.domain.exception.GradeException;
import com.pfplaybackend.api.party.domain.service.PartyroomAggregateService;
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
public class PlaybackCommandService {

    private final PlaybackRepository playbackRepository;
    private final PlaybackAggregationRepository playbackAggregationRepository;
    private final PlaybackQueryService playbackQueryService;
    private final UserActivityPort userActivityPort;
    private final ApplicationEventPublisher eventPublisher;
    private final PartyroomAggregatePort aggregatePort;
    private final ExpirationTaskScheduler scheduleService;
    private final PartyroomAggregateService partyroomAggregateService;
    private final PartyroomQueryService partyroomQueryService;

    private void scheduleTask(PlaybackData playback) {
        long seconds = playback.getDuration().toSeconds();
        PartyroomId partyroomId = playback.getPartyroomId();
        UserId userId = playback.getUserId();
        PlaybackDurationWaitDto playbackDurationWaitMessage = new PlaybackDurationWaitDto(partyroomId, userId);
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
        AuthContext authContext = ThreadLocalContext.getAuthContext();
        ActivePartyroomDto activePartyroomDto = partyroomQueryService.getMyActivePartyroom(authContext.getUserId()).orElseThrow();
        CrewData adjusterCrew = partyroomQueryService.getCrewOrThrow(activePartyroomDto.id(), authContext.getUserId());
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
        PartyroomData partyroom = partyroomQueryService.getPartyroomById(partyroomId);

        if(partyroomAggregateService.hasQueuedDjs(partyroomId.getId())) {
            start(partyroom);
        }else{
            deactivateAndNotify(partyroom);
        }
    }

    public void start(PartyroomData partyroom) {
        List<DjData> queuedDjs = aggregatePort.findDjsOrdered(partyroom.getId());
        int maxAttempts = queuedDjs.size();
        doStart(partyroom, maxAttempts);
    }

    private void doStart(PartyroomData partyroom, int remainingAttempts) {
        partyroomAggregateService.rotateDjQueue(partyroom.getId());

        List<DjData> queuedDjs = aggregatePort.findDjsOrdered(partyroom.getId());
        DjData nextDj = queuedDjs.stream().findFirst().orElseThrow();
        CrewData djCrew = aggregatePort.findCrewById(nextDj.getCrewId().getId()).orElseThrow();
        PlaybackData nextPlayback = playbackQueryService.getNextPlaybackInPlaylist(partyroom.getPartyroomId(), nextDj, djCrew.getUserId());

        if (partyroom.getPlaybackTimeLimit().exceedsDuration(nextPlayback.getDuration())) {
            if (remainingAttempts <= 1) {
                deactivateAndNotify(partyroom);
                return;
            }
            PartyroomData reloaded = partyroomQueryService.getPartyroomById(partyroom.getPartyroomId());
            if (partyroomAggregateService.hasQueuedDjs(reloaded.getId())) {
                doStart(reloaded, remainingAttempts - 1);
            } else {
                deactivateAndNotify(reloaded);
            }
            return;
        }

        PlaybackData playbackData = playbackRepository.save(nextPlayback);
        playbackAggregationRepository.save(PlaybackAggregationData.createFor(playbackData.getId()));
        // Update playback state in PARTYROOM_PLAYBACK
        PartyroomPlaybackData playbackState = aggregatePort.findPlaybackState(partyroom.getId());
        playbackState.updatePlayback(new PlaybackId(playbackData.getId()), new CrewId(djCrew.getId()));
        aggregatePort.savePlaybackState(playbackState);
        // Schedule Task to wait for playback time
        scheduleTask(nextPlayback);
        // Propagation Websocket Event
        PlaybackSnapshot snapshot = new PlaybackSnapshot(
                playbackData.getId(), playbackData.getLinkId(), playbackData.getName(),
                playbackData.getDuration().toDisplayString(), playbackData.getThumbnailImage(), playbackData.getEndTime());
        eventPublisher.publishEvent(new PlaybackStartedEvent(partyroom.getPartyroomId(), new CrewId(djCrew.getId()), snapshot));
        eventPublisher.publishEvent(new DjQueueChangedEvent(partyroom.getPartyroomId()));
    }

    private void deactivateAndNotify(PartyroomData partyroom) {
        partyroomAggregateService.deactivatePlayback(partyroom.getId())
                .forEach(eventPublisher::publishEvent);
    }
}
