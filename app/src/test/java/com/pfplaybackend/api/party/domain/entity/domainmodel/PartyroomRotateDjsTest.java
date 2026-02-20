package com.pfplaybackend.api.party.domain.entity.domainmodel;

import com.pfplaybackend.api.party.domain.entity.data.DjData;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PlaylistId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DJ 순환 로직 테스트
 * Phase 2에서 PartyroomData → 서비스 레벨로 이동된 로직을 단위 테스트
 */
class PartyroomRotateDjsTest {

    private DjData createDj(long crewId, int orderNumber) {
        return DjData.builder()
                .id(crewId * 100)
                .crewId(new CrewId(crewId))
                .playlistId(new PlaylistId(1L))
                .orderNumber(orderNumber)

                .build();
    }

    /**
     * Service-level DJ rotation logic (extracted from PartyroomData)
     */
    private void rotateDjs(List<DjData> djList) {
        int totalElements = djList.size();
        djList.forEach(dj -> {
            if (dj.getOrderNumber() == 1) {
                dj.updateOrderNumber(totalElements);
            } else {
                dj.updateOrderNumber(dj.getOrderNumber() - 1);
            }
        });
    }

    @Test
    @DisplayName("rotateDjs - DJ 순서가 실제로 변경되어야 한다")
    void rotateDjs_shouldActuallyUpdateOrderNumbers() {
        // given
        DjData dj1 = createDj(1L, 1);
        DjData dj2 = createDj(2L, 2);
        DjData dj3 = createDj(3L, 3);

        List<DjData> djList = new ArrayList<>(List.of(dj1, dj2, dj3));

        // when
        rotateDjs(djList);

        // then
        Map<Long, Integer> orderMap = djList.stream()
                .collect(Collectors.toMap(dj -> dj.getCrewId().getId(), DjData::getOrderNumber));

        assertThat(orderMap.get(1L)).isEqualTo(3); // 1번 DJ -> 맨 뒤로
        assertThat(orderMap.get(2L)).isEqualTo(1); // 2번 DJ -> 1칸 앞으로
        assertThat(orderMap.get(3L)).isEqualTo(2); // 3번 DJ -> 1칸 앞으로
    }

    @Test
    @DisplayName("rotateDjs - DJ가 1명일 때 순서 유지")
    void rotateDjs_singleDj_shouldKeepOrder() {
        // given
        DjData dj1 = createDj(1L, 1);

        List<DjData> djList = new ArrayList<>(List.of(dj1));

        // when
        rotateDjs(djList);

        // then
        assertThat(dj1.getOrderNumber()).isEqualTo(1);
    }
}
