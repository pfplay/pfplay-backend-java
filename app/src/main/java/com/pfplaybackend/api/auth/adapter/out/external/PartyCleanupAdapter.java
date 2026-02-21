package com.pfplaybackend.api.auth.adapter.out.external;

import com.pfplaybackend.api.auth.application.port.out.PartyCleanupPort;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomDto;
import com.pfplaybackend.api.party.application.service.PartyroomAccessCommandService;
import com.pfplaybackend.api.party.application.service.PartyroomQueryService;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartyCleanupAdapter implements PartyCleanupPort {

    private final PartyroomQueryService partyroomQueryService;
    private final PartyroomAccessCommandService partyroomAccessCommandService;

    @Override
    public void exitActivePartyroomIfPresent(UserId userId) {
        Optional<ActivePartyroomDto> activePartyroom = partyroomQueryService.getMyActivePartyroom(userId);
        if (activePartyroom.isPresent()) {
            PartyroomId partyroomId = new PartyroomId(activePartyroom.get().id());
            log.info("User {} exiting active partyroom {} on logout", userId, partyroomId);
            partyroomAccessCommandService.exit(partyroomId);
        }
    }
}
