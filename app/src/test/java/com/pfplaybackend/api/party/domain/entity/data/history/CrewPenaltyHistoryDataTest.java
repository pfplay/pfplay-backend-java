package com.pfplaybackend.api.party.domain.entity.data.history;

import com.pfplaybackend.api.party.domain.enums.PenaltyType;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CrewPenaltyHistoryDataTest {

    @Test
    @DisplayName("release — released 상태와 releaserCrewId가 설정된다")
    void release_setsReleasedStateAndReleaserInfo() {
        // given
        CrewPenaltyHistoryData penalty = CrewPenaltyHistoryData.builder()
                .partyroomId(new PartyroomId(1L))
                .punisherCrewId(new CrewId(10L))
                .punishedCrewId(new CrewId(20L))
                .penaltyType(PenaltyType.PERMANENT_EXPULSION)
                .penaltyDate(LocalDateTime.now())
                .released(false)
                .build();

        CrewId releaserCrewId = new CrewId(10L);

        // when
        penalty.release(releaserCrewId);

        // then
        assertThat(penalty.isReleased()).isTrue();
        assertThat(penalty.getReleasedByCrewId()).isEqualTo(releaserCrewId);
        assertThat(penalty.getReleaseDate()).isNotNull();
    }

    @Test
    @DisplayName("release — 이미 released된 상태에서 다시 release하면 상태가 유지된다")
    void release_idempotentWhenAlreadyReleased() {
        // given
        CrewPenaltyHistoryData penalty = CrewPenaltyHistoryData.builder()
                .partyroomId(new PartyroomId(1L))
                .punisherCrewId(new CrewId(10L))
                .punishedCrewId(new CrewId(20L))
                .penaltyType(PenaltyType.PERMANENT_EXPULSION)
                .penaltyDate(LocalDateTime.now())
                .released(false)
                .build();

        penalty.release(new CrewId(10L));

        // when
        CrewId newReleaserId = new CrewId(30L);
        penalty.release(newReleaserId);

        // then
        assertThat(penalty.isReleased()).isTrue();
        assertThat(penalty.getReleasedByCrewId()).isEqualTo(newReleaserId);
    }
}
