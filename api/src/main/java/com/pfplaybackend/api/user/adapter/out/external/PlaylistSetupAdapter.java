package com.pfplaybackend.api.user.adapter.out.external;

import com.pfplaybackend.api.playlist.application.service.PlaylistCommandService;
import com.pfplaybackend.api.user.application.port.out.PlaylistSetupPort;
import com.pfplaybackend.api.common.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaylistSetupAdapter implements PlaylistSetupPort {

    private final PlaylistCommandService playlistCommandService;

    @Override
    public void createDefaultPlaylist(UserId userId) {
        playlistCommandService.createDefaultPlaylist(userId);
    }
}
