package com.pfplaybackend.api.party.application.port.out;

import com.pfplaybackend.api.user.domain.value.UserId;

public interface PlaylistCommandPort {
    void grabMusic(UserId userId, String linkId);
}
