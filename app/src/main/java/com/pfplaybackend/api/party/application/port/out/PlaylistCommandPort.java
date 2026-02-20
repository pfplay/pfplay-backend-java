package com.pfplaybackend.api.party.application.port.out;

import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.playlist.application.dto.PlaybackTrackDto;
import com.pfplaybackend.api.party.domain.value.PlaylistId;

public interface PlaylistCommandPort {
    void grabTrack(UserId userId, String linkId);
    PlaybackTrackDto getFirstTrack(PlaylistId playlistId);
}
