package com.pfplaybackend.api.party.domain.entity.data.history;

import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.party.domain.value.CrewId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CrewBlockHistoryData 엔티티 캡슐화 테스트")
class CrewBlockHistoryDataTest {

    @Test
    @DisplayName("unblock()으로 차단을 해제하면 unblocked=true, unblockDate가 설정된다")
    void unblockSetsUnblockedAndDate() {
        // given
        LocalDateTime beforeUnblock = LocalDateTime.now();
        CrewBlockHistoryData historyData = CrewBlockHistoryData.builder()
                .blockerCrewId(new CrewId(1L))
                .blockedCrewId(new CrewId(2L))
                .blockedUserId(new UserId())
                .blockDate(LocalDateTime.now().minusDays(1))
                .unblocked(false)
                .build();

        // when
        historyData.unblock();

        // then
        assertThat(historyData.isUnblocked()).isTrue();
        assertThat(historyData.getUnblockDate()).isNotNull();
        assertThat(historyData.getUnblockDate()).isAfterOrEqualTo(beforeUnblock);
    }
}
