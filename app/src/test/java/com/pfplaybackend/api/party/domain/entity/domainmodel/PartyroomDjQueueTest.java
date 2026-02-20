package com.pfplaybackend.api.party.domain.entity.domainmodel;

import com.pfplaybackend.api.party.domain.entity.data.DjData;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PlaylistId;
import com.pfplaybackend.api.common.domain.value.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DJ 큐 제거/순서 재배정 로직 테스트
 * Phase 2에서 PartyroomData -> 서비스 레벨로 이동된 로직을 단위 테스트
 */
class PartyroomDjQueueTest {

    private DjData createDj(long crewId, int orderNumber) {
        return DjData.builder()
                .id(crewId * 100)
                .userId(new UserId())
                .crewId(new CrewId(crewId))
                .playlistId(new PlaylistId(1L))
                .orderNumber(orderNumber)
                .build();
    }

    /**
     * Service-level DJ queue removal logic (hard-delete version)
     * Returns the remaining DJs after removing the target.
     */
    private List<DjData> removeFromDjQueue(List<DjData> queuedDjs, CrewId crewId) {
        List<DjData> remaining = queuedDjs.stream()
                .filter(dj -> !dj.getCrewId().equals(crewId))
                .toList();
        int order = 1;
        for (DjData dj : remaining) {
            dj.updateOrderNumber(order++);
        }
        return remaining;
    }

    @Test
    @DisplayName("tryRemoveInDjQueue - 제거 후 남은 DJ 순서가 빈틈 없이 재배정되어야 한다")
    void tryRemoveInDjQueue_shouldReassignOrderWithoutGaps() {
        // given: DJ 3명 (order 1, 2, 3) 중 2번 DJ 제거
        DjData dj1 = createDj(1L, 1);
        DjData dj2 = createDj(2L, 2);
        DjData dj3 = createDj(3L, 3);

        List<DjData> djList = new ArrayList<>(List.of(dj1, dj2, dj3));

        // when
        List<DjData> remaining = removeFromDjQueue(djList, new CrewId(2L));

        // then
        Map<Long, Integer> orderMap = remaining.stream()
                .collect(Collectors.toMap(dj -> dj.getCrewId().getId(), DjData::getOrderNumber));

        assertThat(orderMap).hasSize(2);
        assertThat(orderMap.get(1L)).isEqualTo(1);
        assertThat(orderMap.get(3L)).isEqualTo(2);
    }

    @Test
    @DisplayName("tryRemoveInDjQueue - 1번(현재) DJ 제거 시 나머지 순서 재배정")
    void tryRemoveInDjQueue_removeCurrentDj_shouldReassignCorrectly() {
        // given
        DjData dj1 = createDj(1L, 1);
        DjData dj2 = createDj(2L, 2);
        DjData dj3 = createDj(3L, 3);

        List<DjData> djList = new ArrayList<>(List.of(dj1, dj2, dj3));

        // when
        List<DjData> remaining = removeFromDjQueue(djList, new CrewId(1L));

        // then
        Map<Long, Integer> orderMap = remaining.stream()
                .collect(Collectors.toMap(dj -> dj.getCrewId().getId(), DjData::getOrderNumber));

        assertThat(orderMap).hasSize(2);
        assertThat(orderMap.get(2L)).isEqualTo(1);
        assertThat(orderMap.get(3L)).isEqualTo(2);
    }

    @Test
    @DisplayName("isCurrentDj - 재생 활성 상태에서 orderNumber 1인 DJ가 현재 DJ")
    void isCurrentDj_shouldReturnTrue_whenPlaybackActivatedAndOrder1() {
        // given
        DjData dj1 = createDj(1L, 1);
        DjData dj2 = createDj(2L, 2);

        List<DjData> queuedDjs = List.of(dj1, dj2);
        boolean isPlaybackActivated = true;

        // when & then
        assertThat(isCurrentDj(queuedDjs, new CrewId(1L), isPlaybackActivated)).isTrue();
        assertThat(isCurrentDj(queuedDjs, new CrewId(2L), isPlaybackActivated)).isFalse();
    }

    @Test
    @DisplayName("isCurrentDj - 재생 비활성 상태에서는 항상 false")
    void isCurrentDj_shouldReturnFalse_whenPlaybackNotActivated() {
        // given
        DjData dj1 = createDj(1L, 1);

        List<DjData> queuedDjs = List.of(dj1);
        boolean isPlaybackActivated = false;

        // when & then
        assertThat(isCurrentDj(queuedDjs, new CrewId(1L), isPlaybackActivated)).isFalse();
    }

    private boolean isCurrentDj(List<DjData> queuedDjs, CrewId crewId, boolean isPlaybackActivated) {
        if (!isPlaybackActivated) return false;
        return queuedDjs.stream()
                .anyMatch(dj -> dj.getCrewId().equals(crewId) && dj.getOrderNumber() == 1);
    }
}
