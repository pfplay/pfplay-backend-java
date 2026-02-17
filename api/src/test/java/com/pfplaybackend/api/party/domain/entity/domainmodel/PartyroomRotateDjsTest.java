package com.pfplaybackend.api.party.domain.entity.domainmodel;

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

    private Dj createDj(long crewId, int orderNumber) {
        return Dj.builder()
                .partyroomId(new PartyroomId(1L))
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
        Dj dj1 = createDj(1L, 1);
        Dj dj2 = createDj(2L, 2);
        Dj dj3 = createDj(3L, 3);

        Set<Dj> djSet = new HashSet<>();
        djSet.add(dj1);
        djSet.add(dj2);
        djSet.add(dj3);

        Partyroom partyroom = Partyroom.builder()
                .partyroomId(new PartyroomId(1L))
                .build();
        partyroom.assignDjSet(djSet);

        // when
        partyroom.rotateDjs();

        // then
        Map<Long, Integer> orderMap = partyroom.getDjSet().stream()
                .collect(Collectors.toMap(dj -> dj.getCrewId().getId(), Dj::getOrderNumber));

        assertThat(orderMap.get(1L)).isEqualTo(3); // 1번 DJ -> 맨 뒤로
        assertThat(orderMap.get(2L)).isEqualTo(1); // 2번 DJ -> 1칸 앞으로
        assertThat(orderMap.get(3L)).isEqualTo(2); // 3번 DJ -> 1칸 앞으로
    }

    @Test
    @DisplayName("rotateDjs - DJ가 1명일 때 순서 유지")
    void rotateDjs_singleDj_shouldKeepOrder() {
        // given
        Dj dj1 = createDj(1L, 1);

        Set<Dj> djSet = new HashSet<>();
        djSet.add(dj1);

        Partyroom partyroom = Partyroom.builder()
                .partyroomId(new PartyroomId(1L))
                .build();
        partyroom.assignDjSet(djSet);

        // when
        partyroom.rotateDjs();

        // then
        assertThat(dj1.getOrderNumber()).isEqualTo(1);
    }
}
