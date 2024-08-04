package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.partyroom.application.aspect.context.PartyContext;
import com.pfplaybackend.api.partyroom.application.dto.ActivePartyroomDto;
import com.pfplaybackend.api.partyroom.application.dto.MusicDto;
import com.pfplaybackend.api.partyroom.application.dto.PlaybackDto;
import com.pfplaybackend.api.partyroom.application.peer.MusicQueryPeerService;
import com.pfplaybackend.api.partyroom.application.peer.UserActivityPeerService;
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
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.domain.value.PlaybackId;
import com.pfplaybackend.api.partyroom.domain.value.PlaylistId;
import com.pfplaybackend.api.partyroom.event.RedisMessagePublisher;
import com.pfplaybackend.api.partyroom.event.message.PlaybackMessage;
import com.pfplaybackend.api.partyroom.repository.PartyroomRepository;
import com.pfplaybackend.api.partyroom.repository.PlaybackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class PlaybackManagementService {

    private final PlaybackRepository playbackRepository;
    private final PlaybackConverter playbackConverter;
    private final DjDomainService djDomainService;
    private final PlaybackInfoService playbackInfoService;
    private final PartyroomManagementService partyroomManagementService;
    private final UserActivityPeerService userActivityService;
    private final RedisMessagePublisher redisMessagePublisher;
    private final PartyroomRepository partyroomRepository;
    private final PartyroomConverter partyroomConverter;

    @Transactional
    public void complete(PartyroomId partyroomId) {
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        // Client == Current Dj
        userActivityService.updateDjPointScore(partyContext.getUserId(), 1);
        // FIXME Remove DjDomainService in Here!!!

        PartyroomData partyroomData = partyroomRepository.findById(partyroomId.getId()).orElseThrow();
        Partyroom partyroom = partyroomConverter.toDomain(partyroomData);
        if(djDomainService.isExistDj(partyroom)) {
            start(partyroom);
        }else{
            // 파티룸의 '재생 활성화'상태 끄기
            partyroomManagementService.updatePlaybackActivationStatus(partyroomId, false);
        }
    }

    public void skip(PartyroomId partyroomId) {
        partyroomManagementService.updatePlaybackActivationStatus(partyroomId, false);
    }

    public void start(Partyroom partyroom) {
        // FIXME All Dj 'orderNumber' bulk update
        Partyroom updataedPartyroom = partyroomManagementService.rotateDjQueue(partyroom);
        Dj nextDj = updataedPartyroom.getDjs().stream().min(Comparator.comparingInt(Dj::getOrderNumber)).orElseThrow();
        Partymember djMember = updataedPartyroom.getPartymembers().stream().filter(partymember -> partymember.getUserId().equals(nextDj.getUserId())).toList().get(0);
        Playback nextPlayback = playbackInfoService.getNextPlaybackInPlaylist(updataedPartyroom.getPartyroomId(), nextDj.getPlaylistId());
        PlaybackData playbackData = playbackRepository.save(playbackConverter.toData(nextPlayback));
        // Update 'CurrentPlaybackId'
        partyroomRepository.save(partyroomConverter.toData(partyroom.updatePlaybackId(new PlaybackId(playbackData.getId()))));
        publishPlaybackChangedEvent(updataedPartyroom.getPartyroomId(), djMember.getId(), playbackData);
    }

    // FIXME PartymemberId
    private void publishPlaybackChangedEvent(PartyroomId partyroomId, long partymemberId, PlaybackData playbackData ) {

        redisMessagePublisher.publish(MessageTopic.PLAYBACK,
                new PlaybackMessage(partyroomId, MessageTopic.PLAYBACK, partymemberId,
                        new PlaybackDto(playbackData.getId(), playbackData.getLinkId(), playbackData.getName(), playbackData.getDuration(), playbackData.getThumbnailImage(), playbackData.getEndTime())));
    }
}