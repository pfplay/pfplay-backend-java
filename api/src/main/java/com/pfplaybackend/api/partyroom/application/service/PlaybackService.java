package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.partyroom.application.aspect.context.PartyContext;
import com.pfplaybackend.api.partyroom.application.dto.DjDto;
import com.pfplaybackend.api.partyroom.application.dto.MusicDto;
import com.pfplaybackend.api.partyroom.application.dto.PlaybackDto;
import com.pfplaybackend.api.partyroom.application.peer.MusicQueryPeerService;
import com.pfplaybackend.api.partyroom.application.peer.UserActivityPeerService;
import com.pfplaybackend.api.partyroom.domain.entity.converter.PartyroomConverter;
import com.pfplaybackend.api.partyroom.domain.entity.converter.PlaybackConverter;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Dj;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partymember;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Playback;
import com.pfplaybackend.api.partyroom.domain.enums.MessageTopic;
import com.pfplaybackend.api.partyroom.domain.enums.MotionType;
import com.pfplaybackend.api.partyroom.domain.service.DjDomainService;
import com.pfplaybackend.api.partyroom.domain.value.PartymemberId;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.domain.value.PlaybackId;
import com.pfplaybackend.api.partyroom.domain.value.PlaylistId;
import com.pfplaybackend.api.partyroom.event.RedisMessagePublisher;
import com.pfplaybackend.api.partyroom.event.message.MotionMessage;
import com.pfplaybackend.api.partyroom.event.message.PlaybackMessage;
import com.pfplaybackend.api.partyroom.repository.PartyroomRepository;
import com.pfplaybackend.api.partyroom.repository.PlaybackRepository;
import com.pfplaybackend.api.playlist.application.service.PlaylistMusicService;
import com.pfplaybackend.api.playlist.repository.PlaylistRepository;
import com.pfplaybackend.api.user.application.service.UserActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaybackService {
    // Using Repositories
    private final PartyroomRepository partyroomRepository;
    private final PlaybackRepository playbackRepository;
    // Using Entity Converters
    private final PartyroomConverter partyroomConverter;
    private final PlaybackConverter playbackConverter;
    // Using Domain Services
    private final DjDomainService djDomainService;
    // Using Other Application Services
    private final PartyroomManagementService partyroomManagementService;
    private final PartyroomInfoService partyroomInfoService;
    // Using Peer Services
    private final MusicQueryPeerService musicQueryService;
    private final UserActivityPeerService userActivityService;
    // Using Message Publisher
    private final RedisMessagePublisher redisMessagePublisher;



    @Transactional
    public void complete(PartyroomId partyroomId) {
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        // Partyroom partyroom = new Partyroom();
        // partyroom.getNextDJ();
        if(djDomainService.isExistNextDj()) {
            // Playback nextPlayback = getNextPlayback();
        }else{
            // 파티룸의 '재생 활성화'상태 끄기
            partyroomManagementService.updatePlaybackActivationStatus(partyroomId, false);
        }
        // Client == Current Dj
        userActivityService.updateDjPointScore(partyContext.getUserId(), 1);
    }

    public void skip(PartyroomId partyroomId) {
        partyroomManagementService.updatePlaybackActivationStatus(partyroomId, false);
    }


    public void start(Partyroom partyroom) {
        Dj nextDj = partyroom.getDjs().get(0);
        Partymember nextPartymember = partyroom.getPartymembers().stream().filter(partymember -> partymember.getUserId().equals(nextDj.getUserId())).toList().get(0);
        PlaylistId nextPlaylistId = nextDj.getPlaylistId();
        Playback nextPlayback = getNextPlayback(partyroom.getPartyroomId(), nextPlaylistId);
        // FIXME All Dj 'orderNumber' bulk update
        // Rotate Dj Order
        partyroomRepository.save(partyroomConverter.toData(partyroom.rotateDjs()));
        // Save Playlist Record
        playbackRepository.save(playbackConverter.toData(nextPlayback));
        // Publish Event
        publishPlaybackChangedEvent(partyroom.getPartyroomId(), nextPartymember.getId(), nextPlayback);
    }

    // FIXME PartymemberId
    private void publishPlaybackChangedEvent(PartyroomId partyroomId, long partymemberId, Playback playback) {
        redisMessagePublisher.publish(MessageTopic.PLAYBACK,
                new PlaybackMessage(partyroomId, MessageTopic.PLAYBACK, partymemberId,
                        new PlaybackDto(playback.getId(), playback.getLinkId(), playback.getName(), playback.getDuration(), playback.getThumbnailImage())));
    }

    public Playback getNextPlayback(PartyroomId partyroomId, PlaylistId playlistId) {
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        MusicDto musicDto = musicQueryService.getFirstMusic(playlistId);
        return Playback.create(partyroomId , partyContext.getUserId(), musicDto);
    }

    public PlaybackId getCurrentPlaybackId() {
        return partyroomInfoService.getMyActivePartyroom().getCurrentPlaybackId();
    }

    public void updateAggregation(Playback playback) {
        playbackRepository.save(playbackConverter.toData(playback));
    }
}
