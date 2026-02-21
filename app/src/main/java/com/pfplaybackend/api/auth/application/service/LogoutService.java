package com.pfplaybackend.api.auth.application.service;

import com.pfplaybackend.api.auth.application.port.out.PartyCleanupPort;
import com.pfplaybackend.api.common.config.security.jwt.CustomJwtAuthenticationToken;
import com.pfplaybackend.api.common.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutService {

    private final PartyCleanupPort partyCleanupPort;

    public void exitActivePartyroomIfPresent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication instanceof CustomJwtAuthenticationToken token)) {
            return;
        }

        UserId userId = token.getUserId();
        partyCleanupPort.exitActivePartyroomIfPresent(userId);
    }
}
