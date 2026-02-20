package com.pfplaybackend.api.playlist.domain.entity.data;

import com.pfplaybackend.api.common.domain.value.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TrackData 엔티티 캡슐화 테스트")
class TrackDataTest {

    @Test
    @DisplayName("reorder()로 트랙 순서를 변경할 수 있다")
    void reorder_changesOrderNumber() {
        // given
        TrackData track = TrackData.builder()
                .playlistId(1L)
                .orderNumber(1)
                .name("테스트 곡")
                .linkId("link1")
                .duration(Duration.fromString("03:30"))
                .thumbnailImage("thumb.jpg")
                .build();

        // when
        track.reorder(5);

        // then
        assertThat(track.getOrderNumber()).isEqualTo(5);
    }

    @Test
    @DisplayName("moveToPlaylist()로 트랙을 다른 플레이리스트로 이동할 수 있다")
    void moveToPlaylist_changesPlaylistAndOrder() {
        // given
        TrackData track = TrackData.builder()
                .playlistId(1L)
                .orderNumber(2)
                .name("테스트 곡")
                .linkId("link1")
                .duration(Duration.fromString("04:00"))
                .thumbnailImage("thumb.jpg")
                .build();

        // when
        track.moveToPlaylist(2L, 3);

        // then
        assertThat(track.getPlaylistId()).isEqualTo(2L);
        assertThat(track.getOrderNumber()).isEqualTo(3);
    }
}
