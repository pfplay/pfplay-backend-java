package com.pfplaybackend.api.user.application.port.out;

import com.pfplaybackend.api.user.domain.value.UserId;

public interface PlaylistSetupPort {
    void createDefaultPlaylist(UserId userId);
}
