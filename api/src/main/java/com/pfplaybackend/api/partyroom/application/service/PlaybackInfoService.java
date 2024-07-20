package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.partyroom.application.aspect.context.PartyContext;
import com.pfplaybackend.api.partyroom.application.dto.MusicDto;
import com.pfplaybackend.api.partyroom.application.peer.MusicQueryPeerService;
import com.pfplaybackend.api.partyroom.domain.entity.converter.PlaybackConverter;
import com.pfplaybackend.api.partyroom.domain.entity.data.PlaybackData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Playback;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.domain.value.PlaybackId;
import com.pfplaybackend.api.partyroom.domain.value.PlaylistId;
import com.pfplaybackend.api.partyroom.repository.PlaybackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaybackInfoService {

    private final PlaybackRepository playbackRepository;
    private final PlaybackConverter playbackConverter;
    private final MusicQueryPeerService musicQueryService;

    public Playback getNextPlaybackInPlaylist(PartyroomId partyroomId, PlaylistId playlistId) {
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        MusicDto musicDto = musicQueryService.getFirstMusic(playlistId);
        return Playback.create(partyroomId , partyContext.getUserId(), musicDto);
    }

    public void updatePlaybackAggregation(Playback playback, List<Integer> deltaRecord) {
        Playback updatedPlayback = playback.updateAggregation(deltaRecord.get(0), deltaRecord.get(1), deltaRecord.get(2));
        playbackRepository.save(playbackConverter.toData(updatedPlayback));
    }

    public Playback getPlaybackById(PlaybackId playbackId) {
        PlaybackData playbackData = playbackRepository.findById(playbackId.getId()).orElseThrow();
        return playbackConverter.toDomain(playbackData);
    }
}
