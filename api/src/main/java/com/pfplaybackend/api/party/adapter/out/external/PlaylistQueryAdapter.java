package com.pfplaybackend.api.party.adapter.out.external;

import com.pfplaybackend.api.party.application.dto.playback.MusicDto;
import com.pfplaybackend.api.party.application.port.out.PlaylistQueryPort;
import com.pfplaybackend.api.party.domain.value.PlaylistId;
import com.pfplaybackend.api.playlist.application.service.TrackQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaylistQueryAdapter implements PlaylistQueryPort {
    private final TrackQueryService trackQueryService;

    @Override
    public MusicDto getFirstMusic(PlaylistId playlistId) {
        return trackQueryService.getFirstMusic(playlistId.getId());
    }

    @Override
    public boolean isEmptyPlaylist(Long playlistId) {
        return trackQueryService.isEmptyPlaylist(playlistId);
    }
}
