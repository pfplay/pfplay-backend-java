package com.pfplaybackend.api.party.application.dto.playback;

import com.pfplaybackend.api.common.domain.value.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlaybackDtoTest {

    private static final Duration DURATION_3_45 = Duration.fromString("3:45");

    @Test
    @DisplayName("withEndTime 팩토리로 생성 시 endTime이 설정되어야 한다")
    void withEndTimeShouldSetEndTime() {
        // when
        PlaybackDto dto = PlaybackDto.withEndTime(1L, "linkId", "name", DURATION_3_45, "thumb.jpg", 123456789L);

        // then
        assertThat(dto.getEndTime()).isEqualTo(123456789L);
        assertThat(dto.getName()).isEqualTo("name");
        assertThat(dto.getLinkId()).isEqualTo("linkId");
    }

    @Test
    @DisplayName("QueryProjection 생성자로 생성 시 기본 필드가 설정되어야 한다")
    void queryProjectionConstructor() {
        // when
        PlaybackDto dto = new PlaybackDto(1L, "linkId", "name", DURATION_3_45, "thumb.jpg");

        // then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDuration()).isEqualTo(DURATION_3_45);
        assertThat(dto.getThumbnailImage()).isEqualTo("thumb.jpg");
    }
}
