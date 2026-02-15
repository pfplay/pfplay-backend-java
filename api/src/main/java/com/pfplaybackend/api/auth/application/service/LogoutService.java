package com.pfplaybackend.api.auth.application.service;

import com.pfplaybackend.api.common.config.security.jwt.CustomJwtAuthenticationToken;
import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomWithCrewDto;
import com.pfplaybackend.api.party.application.service.PartyroomAccessService;
import com.pfplaybackend.api.party.application.service.PartyroomInfoService;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutService {

    private final PartyroomInfoService partyroomInfoService;
    private final PartyroomAccessService partyroomAccessService;

    public void exitActivePartyroomIfPresent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication instanceof CustomJwtAuthenticationToken token)) {
            return;
        }

        UserId userId = token.getUserId();
        Optional<ActivePartyroomWithCrewDto> activePartyroom = partyroomInfoService.getMyActivePartyroomWithCrewId(userId);

        if (activePartyroom.isPresent()) {
            PartyroomId partyroomId = new PartyroomId(activePartyroom.get().getId());
            log.info("User {} exiting active partyroom {} on logout", userId, partyroomId);
            partyroomAccessService.exit(partyroomId);
        }
    }
}
