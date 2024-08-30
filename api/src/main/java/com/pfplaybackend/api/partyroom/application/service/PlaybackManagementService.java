package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.partyroom.application.dto.PlaybackDto;
import com.pfplaybackend.api.partyroom.application.peer.UserActivityPeerService;
import com.pfplaybackend.api.partyroom.application.service.task.TaskScheduleService;
import com.pfplaybackend.api.partyroom.domain.entity.converter.PartyroomConverter;
import com.pfplaybackend.api.partyroom.domain.entity.converter.PlaybackConverter;
import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.domain.entity.data.PlaybackData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Dj;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partymember;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Playback;
import com.pfplaybackend.api.partyroom.domain.enums.MessageTopic;
import com.pfplaybackend.api.partyroom.domain.service.DjDomainService;
import com.pfplaybackend.api.partyroom.domain.service.PlaybackDomainService;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.domain.value.PlaybackId;
import com.pfplaybackend.api.partyroom.event.RedisMessagePublisher;
import com.pfplaybackend.api.partyroom.event.message.TaskWaitMessage;
import com.pfplaybackend.api.partyroom.event.message.PlaybackMessage;
import com.pfplaybackend.api.partyroom.repository.PartyroomRepository;
import com.pfplaybackend.api.partyroom.repository.PlaybackRepository;
import com.pfplaybackend.api.user.domain.value.UserId;
import io.lettuce.core.ScriptOutputType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.UUID;
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

    private void scheduleTask(Playback playback) {
        long seconds = playbackDomainService.convertToSeconds(playback.getDuration());
        PartyroomId partyroomId = playback.getPartyroomId();
        UserId userId = playback.getUserId();
        TaskWaitMessage taskWaitMessage = new TaskWaitMessage(partyroomId, userId);
        scheduleService.setKeyWithExpiration(UUID.randomUUID().toString(), taskWaitMessage, seconds, TimeUnit.SECONDS);
    }

    @Transactional
    public void complete(PartyroomId partyroomId, UserId userId) {
        tryProceed(partyroomId);
        userActivityService.updateDjPointScore(userId, 1);
    }

    @Transactional
    public void skip(PartyroomId partyroomId) {
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
        Partymember djMember = updataedPartyroom.getPartymembers().stream().filter(partymember -> partymember.getUserId().equals(nextDj.getUserId())).toList().get(0);
        Playback nextPlayback = playbackInfoService.getNextPlaybackInPlaylist(updataedPartyroom.getPartyroomId(), nextDj);
        PlaybackData playbackData = playbackRepository.save(playbackConverter.toData(nextPlayback));
        // Update 'CurrentPlaybackId'
        partyroomRepository.save(partyroomConverter.toData(partyroom.updatePlaybackId(new PlaybackId(playbackData.getId()))));

        // Schedule Task to wait for playback time
        scheduleTask(nextPlayback);

        // Propagation Websocket Event
        publishPlaybackChangedEvent(updataedPartyroom.getPartyroomId(), djMember.getId(), playbackData);
    }

    // FIXME PartymemberId
    private void publishPlaybackChangedEvent(PartyroomId partyroomId, long partymemberId, PlaybackData playbackData ) {
        redisMessagePublisher.publish(MessageTopic.PLAYBACK,
                new PlaybackMessage(partyroomId, MessageTopic.PLAYBACK, partymemberId,
                        new PlaybackDto(playbackData.getId(), playbackData.getLinkId(), playbackData.getName(), playbackData.getDuration(), playbackData.getThumbnailImage(), playbackData.getEndTime())));
    }
}