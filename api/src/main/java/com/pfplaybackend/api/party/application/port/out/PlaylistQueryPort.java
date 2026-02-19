package com.pfplaybackend.api.party.application.port.out;

import com.pfplaybackend.api.party.application.dto.playback.MusicDto;
import com.pfplaybackend.api.party.domain.value.PlaylistId;

public interface PlaylistQueryPort {
    MusicDto getFirstMusic(PlaylistId playlistId);
    boolean isEmptyPlaylist(Long playlistId);
}
