package com.pfplaybackend.api.party.application.dto.playback;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlaybackDtoTest {

    @Test
    @DisplayName("withEndTime 팩토리로 생성 시 반응 수가 0으로 초기화되어야 한다")
    void withEndTime_shouldInitializeReactionCountsToZero() {
        // when
        PlaybackDto dto = PlaybackDto.withEndTime(1L, "linkId", "name", "3:45", "thumb.jpg", 123456789L);

        // then
        assertThat(dto.getLikeCount()).isZero();
        assertThat(dto.getDislikeCount()).isZero();
        assertThat(dto.getGrabCount()).isZero();
    }

    @Test
    @DisplayName("PlaybackDto에 likeCount, dislikeCount, grabCount 필드가 존재해야 한다")
    void shouldHaveReactionCountFields() {
        // given
        PlaybackDto dto = new PlaybackDto();

        // when
        dto.setLikeCount(5);
        dto.setDislikeCount(2);
        dto.setGrabCount(3);

        // then
        assertThat(dto.getLikeCount()).isEqualTo(5);
        assertThat(dto.getDislikeCount()).isEqualTo(2);
        assertThat(dto.getGrabCount()).isEqualTo(3);
    }
}
