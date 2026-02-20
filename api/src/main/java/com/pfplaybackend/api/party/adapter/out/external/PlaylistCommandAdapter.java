package com.pfplaybackend.api.party.adapter.out.external;

import com.pfplaybackend.api.party.application.dto.playback.PlaybackTrackDto;
import com.pfplaybackend.api.party.application.port.out.PlaylistCommandPort;
import com.pfplaybackend.api.party.domain.value.PlaylistId;
import com.pfplaybackend.api.playlist.application.service.GrabTrackService;
import com.pfplaybackend.api.playlist.application.service.TrackCommandService;
import com.pfplaybackend.api.common.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaylistCommandAdapter implements PlaylistCommandPort {

    private final GrabTrackService grabTrackService;
    private final TrackCommandService trackCommandService;

    @Override
    public void grabTrack(UserId userId, String linkId) {
        grabTrackService.grabTrack(userId, linkId);
    }

    @Override
    public PlaybackTrackDto getFirstTrack(PlaylistId playlistId) {
        return trackCommandService.getFirstTrack(playlistId.getId());
    }
}
