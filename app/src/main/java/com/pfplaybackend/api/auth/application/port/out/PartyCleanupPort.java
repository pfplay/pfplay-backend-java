package com.pfplaybackend.api.auth.application.port.out;

import com.pfplaybackend.api.common.domain.value.UserId;

public interface PartyCleanupPort {
    void exitActivePartyroomIfPresent(UserId userId);
}
