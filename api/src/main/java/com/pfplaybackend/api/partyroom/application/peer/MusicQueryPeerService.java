package com.pfplaybackend.api.partyroom.application.peer;

import com.pfplaybackend.api.partyroom.application.dto.playback.MusicDto;
import com.pfplaybackend.api.partyroom.domain.value.PlaylistId;

public interface MusicQueryPeerService {
    MusicDto getFirstMusic(PlaylistId playlistId);
    boolean isEmptyPlaylist(Long playlistId);
}