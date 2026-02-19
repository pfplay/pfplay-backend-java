package com.pfplaybackend.api.party.domain.entity.domainmodel;

import com.pfplaybackend.api.party.domain.entity.data.DjData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.value.PlaylistId;
import com.pfplaybackend.api.user.domain.value.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class PartyroomRotateDjsTest {

    private DjData createDj(long crewId, int orderNumber) {
        return DjData.builder()
                .id(crewId * 100)
                .userId(new UserId())
                .crewId(new CrewId(crewId))
                .playlistId(new PlaylistId(1L))
                .orderNumber(orderNumber)
                .isQueued(true)
                .build();
    }

    @Test
    @DisplayName("rotateDjs - DJ 순서가 실제로 변경되어야 한다")
    void rotateDjs_shouldActuallyUpdateOrderNumbers() {
        // given
        DjData dj1 = createDj(1L, 1);
        DjData dj2 = createDj(2L, 2);
        DjData dj3 = createDj(3L, 3);

        Set<DjData> djSet = new HashSet<>();
        djSet.add(dj1);
        djSet.add(dj2);
        djSet.add(dj3);

        PartyroomData partyroom = PartyroomData.builder()
                .partyroomId(new PartyroomId(1L))
                .build();
        partyroom.assignDjDataSet(djSet);

        // when
        partyroom.rotateDjs();

        // then
        Map<Long, Integer> orderMap = partyroom.getDjDataSet().stream()
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

        Set<DjData> djSet = new HashSet<>();
        djSet.add(dj1);

        PartyroomData partyroom = PartyroomData.builder()
                .partyroomId(new PartyroomId(1L))
                .build();
        partyroom.assignDjDataSet(djSet);

        // when
        partyroom.rotateDjs();

        // then
        assertThat(dj1.getOrderNumber()).isEqualTo(1);
    }
}
