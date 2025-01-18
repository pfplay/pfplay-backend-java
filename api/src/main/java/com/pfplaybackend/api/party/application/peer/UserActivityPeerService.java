package com.pfplaybackend.api.party.application.peer;

import com.pfplaybackend.api.user.domain.value.UserId;

public interface UserActivityPeerService {
    void updateDjPointScore(UserId userId, int score);
}