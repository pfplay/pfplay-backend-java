package com.pfplaybackend.api.party.application.peer;

import com.pfplaybackend.api.party.application.dto.playback.MusicDto;
import com.pfplaybackend.api.party.domain.value.PlaylistId;

public interface MusicQueryPeerService {
    MusicDto getFirstMusic(PlaylistId playlistId);
    boolean isEmptyPlaylist(Long playlistId);
}