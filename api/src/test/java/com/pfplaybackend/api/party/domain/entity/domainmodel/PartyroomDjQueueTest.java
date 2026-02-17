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

class PartyroomDjQueueTest {

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
    @DisplayName("tryRemoveInDjQueue - 제거 후 남은 DJ 순서가 빈틈 없이 재배정되어야 한다")
    void tryRemoveInDjQueue_shouldReassignOrderWithoutGaps() {
        // given: DJ 3명 (order 1, 2, 3) 중 2번 DJ 제거
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
        partyroom.tryRemoveInDjQueue(new CrewId(2L));

        // then
        Map<Long, Integer> orderMap = partyroom.getDjSet().stream()
                .filter(Dj::isQueued)
                .collect(Collectors.toMap(dj -> dj.getCrewId().getId(), Dj::getOrderNumber));

        assertThat(orderMap).hasSize(2);
        assertThat(orderMap.get(1L)).isEqualTo(1);
        assertThat(orderMap.get(3L)).isEqualTo(2); // 3번이 2번으로 당겨져야 함
    }

    @Test
    @DisplayName("tryRemoveInDjQueue - 1번(현재) DJ 제거 시 나머지 순서 재배정")
    void tryRemoveInDjQueue_removeCurrentDj_shouldReassignCorrectly() {
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
        partyroom.tryRemoveInDjQueue(new CrewId(1L));

        // then
        Map<Long, Integer> orderMap = partyroom.getDjSet().stream()
                .filter(Dj::isQueued)
                .collect(Collectors.toMap(dj -> dj.getCrewId().getId(), Dj::getOrderNumber));

        assertThat(orderMap).hasSize(2);
        assertThat(orderMap.get(2L)).isEqualTo(1);
        assertThat(orderMap.get(3L)).isEqualTo(2);
    }

    @Test
    @DisplayName("isCurrentDj - 재생 활성 상태에서 orderNumber 1인 DJ가 현재 DJ")
    void isCurrentDj_shouldReturnTrue_whenPlaybackActivatedAndOrder1() {
        // given
        Dj dj1 = createDj(1L, 1);
        Dj dj2 = createDj(2L, 2);

        Set<Dj> djSet = new HashSet<>();
        djSet.add(dj1);
        djSet.add(dj2);

        Partyroom partyroom = Partyroom.builder()
                .partyroomId(new PartyroomId(1L))
                .isPlaybackActivated(true)
                .build();
        partyroom.assignDjSet(djSet);

        // when & then
        assertThat(partyroom.isCurrentDj(new CrewId(1L))).isTrue();
        assertThat(partyroom.isCurrentDj(new CrewId(2L))).isFalse();
    }

    @Test
    @DisplayName("isCurrentDj - 재생 비활성 상태에서는 항상 false")
    void isCurrentDj_shouldReturnFalse_whenPlaybackNotActivated() {
        // given
        Dj dj1 = createDj(1L, 1);

        Set<Dj> djSet = new HashSet<>();
        djSet.add(dj1);

        Partyroom partyroom = Partyroom.builder()
                .partyroomId(new PartyroomId(1L))
                .isPlaybackActivated(false)
                .build();
        partyroom.assignDjSet(djSet);

        // when & then
        assertThat(partyroom.isCurrentDj(new CrewId(1L))).isFalse();
    }
}
