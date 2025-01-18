package com.pfplaybackend.api.party.application.peer;

import com.pfplaybackend.api.user.domain.value.UserId;

public interface GrabMusicPeerService {
    void grabMusic(UserId userId, String linkId);
}