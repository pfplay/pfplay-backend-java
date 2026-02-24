package com.pfplaybackend.api.party.domain.specification;

import com.pfplaybackend.api.common.exception.http.ForbiddenException;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PartyroomEntrySpecificationTest {

    private PartyroomEntrySpecification spec;

    @BeforeEach
    void setUp() {
        spec = new PartyroomEntrySpecification();
    }

    private PartyroomData activePartyroom() {
        return PartyroomData.builder().isTerminated(false).build();
    }

    private PartyroomData terminatedPartyroom() {
        return PartyroomData.builder().isTerminated(true).build();
    }

    @Test
    @DisplayName("정상 입장 — 예외 없음")
    void validEntry() {
        PartyroomData partyroom = activePartyroom();
        assertThatNoException().isThrownBy(() ->
                spec.validate(partyroom, 10, Optional.empty()));
    }

    @Test
    @DisplayName("종료된 파티룸 입장 — ALREADY_TERMINATED")
    void terminatedRoom() {
        PartyroomData partyroom = terminatedPartyroom();
        assertThatThrownBy(() -> spec.validate(partyroom, 10, Optional.empty()))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("정원 초과 — EXCEEDED_LIMIT")
    void exceededCapacity() {
        PartyroomData partyroom = activePartyroom();
        assertThatThrownBy(() -> spec.validate(partyroom, 50, Optional.empty()))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("밴 당한 유저 재입장 — PERMANENT_EXPULSION")
    void bannedUser() {
        CrewData bannedCrew = CrewData.builder().isBanned(true).build();
        PartyroomData partyroom = activePartyroom();
        Optional<CrewData> banned = Optional.of(bannedCrew);
        assertThatThrownBy(() -> spec.validate(partyroom, 10, banned))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("밴 해제된 유저 입장 — 예외 없음")
    void unbannedUser() {
        CrewData unbannedCrew = CrewData.builder().isBanned(false).build();
        PartyroomData partyroom = activePartyroom();
        Optional<CrewData> unbanned = Optional.of(unbannedCrew);
        assertThatNoException().isThrownBy(() ->
                spec.validate(partyroom, 10, unbanned));
    }
}
