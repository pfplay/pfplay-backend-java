package com.pfplaybackend.api.partyroom.application.peer;

import com.pfplaybackend.api.partyroom.application.dto.MusicDto;
import com.pfplaybackend.api.partyroom.domain.value.PlaylistId;

public interface MusicQueryPeerService {
    MusicDto getFirstMusic(PlaylistId playlistId);
}