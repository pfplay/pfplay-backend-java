package com.pfplaybackend.api.party.application.port.out;

import com.pfplaybackend.api.user.domain.value.UserId;

public interface UserActivityPort {
    void updateDjPointScore(UserId userId, int score);
}
