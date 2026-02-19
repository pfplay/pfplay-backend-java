package com.pfplaybackend.api.party.adapter.out.external;

import com.pfplaybackend.api.party.application.port.out.PlaylistCommandPort;
import com.pfplaybackend.api.playlist.application.service.GrabMusicService;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaylistCommandAdapter implements PlaylistCommandPort {

    private final GrabMusicService grabMusicService;

    @Override
    public void grabMusic(UserId userId, String linkId) {
        // TODO Require PlaylistMusicId
        grabMusicService.grabMusic(userId, linkId);
    }
}
