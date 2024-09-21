package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.partyroom.application.aspect.context.PartyContext;
import com.pfplaybackend.api.partyroom.application.dto.ActivePartyroomDto;
import com.pfplaybackend.api.partyroom.application.dto.PlaybackDto;
import com.pfplaybackend.api.partyroom.application.peer.UserActivityPeerService;
import com.pfplaybackend.api.partyroom.application.service.task.TaskScheduleService;
import com.pfplaybackend.api.partyroom.domain.entity.converter.PartyroomConverter;
import com.pfplaybackend.api.partyroom.domain.entity.converter.PlaybackConverter;
import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.domain.entity.data.PlaybackData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Dj;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Crew;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Playback;
import com.pfplaybackend.api.partyroom.domain.enums.MessageTopic;
import com.pfplaybackend.api.partyroom.domain.service.CrewDomainService;
import com.pfplaybackend.api.partyroom.domain.service.DjDomainService;
import com.pfplaybackend.api.partyroom.domain.service.PlaybackDomainService;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.domain.value.PlaybackId;
import com.pfplaybackend.api.partyroom.event.RedisMessagePublisher;
import com.pfplaybackend.api.partyroom.event.message.TaskWaitMessage;
import com.pfplaybackend.api.partyroom.event.message.PlaybackMessage;
import com.pfplaybackend.api.partyroom.exception.GradeException;
import com.pfplaybackend.api.partyroom.repository.PartyroomRepository;
import com.pfplaybackend.api.partyroom.repository.PlaybackRepository;
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
    private final PlaybackConverter playbackConverter;
    private final PlaybackDomainService playbackDomainService;
    private final DjDomainService djDomainService;
    private final PlaybackInfoService playbackInfoService;
    private final PartyroomManagementService partyroomManagementService;
    private final UserActivityPeerService userActivityService;
    private final RedisMessagePublisher redisMessagePublisher;
    private final PartyroomRepository partyroomRepository;
    private final PartyroomConverter partyroomConverter;
    private final TaskScheduleService scheduleService;
    private final CrewDomainService domainService;
    private final CrewDomainService crewDomainService;

    private void scheduleTask(Playback playback) {
        long seconds = playbackDomainService.convertToSeconds(playback.getDuration());
        PartyroomId partyroomId = playback.getPartyroomId();
        UserId userId = playback.getUserId();
        TaskWaitMessage taskWaitMessage = new TaskWaitMessage(partyroomId, userId);
        scheduleService.setKeyWithExpiration(String.valueOf(partyroomId.getId()), taskWaitMessage, seconds, TimeUnit.SECONDS);
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
    public void skip(PartyroomId partyroomId) {
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        ActivePartyroomDto activePartyroomDto = partyroomRepository.getActivePartyroomByUserId(partyContext.getUserId()).orElseThrow();
        PartyroomData partyroomData = partyroomRepository.findById(activePartyroomDto.getId()).orElseThrow();
        Partyroom partyroom = partyroomConverter.toDomain(partyroomData);
        if(crewDomainService.isBelowManagerGrade(partyroom, partyContext.getUserId())) throw ExceptionCreator.create(GradeException.MANAGER_GRADE_REQUIRED);

        cancelTask(partyroomId);
        tryProceed(partyroomId);
    }

    private void tryProceed(PartyroomId partyroomId) {
        PartyroomData partyroomData = partyroomRepository.findById(partyroomId.getId()).orElseThrow();
        Partyroom partyroom = partyroomConverter.toDomain(partyroomData);
        // FIXME Remove DjDomainService in Here!!!
        if(djDomainService.isExistDj(partyroom)) {
            start(partyroom);
        }else{
            partyroomManagementService.updatePlaybackDeactivation(partyroomId);
        }
    }

    public void start(Partyroom partyroom) {
        // FIXME All Dj 'orderNumber' bulk update
        Partyroom updataedPartyroom = partyroomManagementService.rotateDjQueue(partyroom);
        Dj nextDj = updataedPartyroom.getDjs().stream().min(Comparator.comparingInt(Dj::getOrderNumber)).orElseThrow();
        Crew djCrew = updataedPartyroom.getCrews().stream().filter(crew -> crew.getUserId().equals(nextDj.getUserId())).toList().get(0);
        Playback nextPlayback = playbackInfoService.getNextPlaybackInPlaylist(updataedPartyroom.getPartyroomId(), nextDj);
        PlaybackData playbackData = playbackRepository.save(playbackConverter.toData(nextPlayback));
        // Update 'CurrentPlaybackId'
        partyroomRepository.save(partyroomConverter.toData(partyroom.updatePlaybackId(new PlaybackId(playbackData.getId()))));

        // Schedule Task to wait for playback time
        scheduleTask(nextPlayback);

        // Propagation Websocket Event
        publishPlaybackChangedEvent(updataedPartyroom.getPartyroomId(), djCrew.getId(), playbackData);
    }

    // FIXME CrewId
    private void publishPlaybackChangedEvent(PartyroomId partyroomId, long crewId, PlaybackData playbackData ) {
        redisMessagePublisher.publish(MessageTopic.PLAYBACK,
                new PlaybackMessage(partyroomId, MessageTopic.PLAYBACK, crewId,
                        new PlaybackDto(playbackData.getId(), playbackData.getLinkId(), playbackData.getName(), playbackData.getDuration(), playbackData.getThumbnailImage(), playbackData.getEndTime())));
    }
}