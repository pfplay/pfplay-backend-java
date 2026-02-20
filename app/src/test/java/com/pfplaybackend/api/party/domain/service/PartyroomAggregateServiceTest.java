package com.pfplaybackend.api.party.domain.service;

import com.pfplaybackend.api.party.domain.entity.data.DjData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomPlaybackData;
import com.pfplaybackend.api.party.domain.port.DjLoadPort;
import com.pfplaybackend.api.party.domain.port.PlaybackStatePort;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PlaybackId;
import com.pfplaybackend.api.party.domain.value.PlaylistId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartyroomAggregateServiceTest {

    @Mock private DjLoadPort djLoadPort;
    @Mock private PlaybackStatePort playbackStatePort;

    @InjectMocks
    private PartyroomAggregateService service;

    private DjData createDj(Long id, CrewId crewId, int orderNumber) {
        return DjData.builder()
                .id(id)
                .crewId(crewId)
                .playlistId(new PlaylistId(1L))
                .orderNumber(orderNumber)
                .build();
    }

    @Nested
    @DisplayName("removeDjFromQueue")
    class RemoveDjFromQueue {

        @Test
        @DisplayName("대상 DJ를 삭제하고 나머지 순서를 재배치한다")
        void removesTargetAndReorders() {
            // given
            CrewId targetCrewId = new CrewId(2L);
            DjData dj1 = createDj(1L, new CrewId(1L), 1);
            DjData dj2 = createDj(2L, targetCrewId, 2);
            DjData dj3 = createDj(3L, new CrewId(3L), 3);
            List<DjData> djs = new ArrayList<>(List.of(dj1, dj2, dj3));

            when(djLoadPort.findByPartyroomIdOrdered(10L)).thenReturn(djs);

            // when
            service.removeDjFromQueue(10L, targetCrewId);

            // then
            verify(djLoadPort).removeAll(List.of(dj2));
            assertThat(dj1.getOrderNumber()).isEqualTo(1);
            assertThat(dj3.getOrderNumber()).isEqualTo(2);
            verify(djLoadPort).saveAll(List.of(dj1, dj3));
        }

        @Test
        @DisplayName("큐에 DJ가 없으면 아무 일도 하지 않는다")
        void emptyQueueDoesNothing() {
            // given
            when(djLoadPort.findByPartyroomIdOrdered(10L)).thenReturn(Collections.emptyList());

            // when
            service.removeDjFromQueue(10L, new CrewId(1L));

            // then
            verify(djLoadPort).removeAll(Collections.emptyList());
            verify(djLoadPort).saveAll(Collections.emptyList());
        }
    }

    @Nested
    @DisplayName("rotateDjQueue")
    class RotateDjQueue {

        @Test
        @DisplayName("1번 DJ를 마지막으로, 나머지를 한 칸씩 앞으로 이동한다")
        void rotatesCorrectly() {
            // given
            DjData dj1 = createDj(1L, new CrewId(1L), 1);
            DjData dj2 = createDj(2L, new CrewId(2L), 2);
            DjData dj3 = createDj(3L, new CrewId(3L), 3);
            List<DjData> djs = new ArrayList<>(List.of(dj1, dj2, dj3));

            when(djLoadPort.findByPartyroomIdOrdered(10L)).thenReturn(djs);

            // when
            service.rotateDjQueue(10L);

            // then
            assertThat(dj1.getOrderNumber()).isEqualTo(3); // 1->last
            assertThat(dj2.getOrderNumber()).isEqualTo(1); // 2->1
            assertThat(dj3.getOrderNumber()).isEqualTo(2); // 3->2
            verify(djLoadPort).saveAll(djs);
        }
    }

    @Nested
    @DisplayName("deactivatePlayback")
    class DeactivatePlayback {

        @Test
        @DisplayName("playbackState를 비활성화하고 모든 DJ를 삭제한다")
        void deactivatesAndDeletesAll() {
            // given
            PartyroomPlaybackData playbackState = PartyroomPlaybackData.createFor(10L);
            playbackState.activate(new PlaybackId(1L), new CrewId(1L));

            DjData dj1 = createDj(1L, new CrewId(1L), 1);
            DjData dj2 = createDj(2L, new CrewId(2L), 2);
            List<DjData> djs = new ArrayList<>(List.of(dj1, dj2));

            when(playbackStatePort.findByPartyroomId(10L)).thenReturn(playbackState);
            when(djLoadPort.findByPartyroomIdOrdered(10L)).thenReturn(djs);

            // when
            service.deactivatePlayback(10L);

            // then
            assertThat(playbackState.isActivated()).isFalse();
            verify(playbackStatePort).save(playbackState);
            verify(djLoadPort).removeAll(djs);
        }
    }

    @Nested
    @DisplayName("hasQueuedDjs")
    class HasQueuedDjs {

        @Test
        @DisplayName("큐에 DJ가 있으면 true를 반환한다")
        void returnsTrueWhenDjsExist() {
            // given
            when(djLoadPort.existsByPartyroomId(10L)).thenReturn(true);

            // when / then
            assertThat(service.hasQueuedDjs(10L)).isTrue();
        }

        @Test
        @DisplayName("큐에 DJ가 없으면 false를 반환한다")
        void returnsFalseWhenNoDjs() {
            // given
            when(djLoadPort.existsByPartyroomId(10L)).thenReturn(false);

            // when / then
            assertThat(service.hasQueuedDjs(10L)).isFalse();
        }
    }
}
