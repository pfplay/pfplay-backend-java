package com.pfplaybackend.api.party.domain.entity.data;

import com.pfplaybackend.api.common.domain.value.Duration;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlaybackDataTest {

    @Test
    @DisplayName("create — 팩토리 메서드가 모든 필드를 올바르게 설정한다")
    void createSetsAllFields() {
        // given
        PartyroomId partyroomId = new PartyroomId(1L);
        UserId userId = new UserId(10L);

        // when
        PlaybackData playback = PlaybackData.create(
                partyroomId, userId,
                "Test Song", "3:30", "linkId123", "thumb.jpg"
        );

        // then
        assertThat(playback.getPartyroomId()).isEqualTo(partyroomId);
        assertThat(playback.getUserId()).isEqualTo(userId);
        assertThat(playback.getName()).isEqualTo("Test Song");
        assertThat(playback.getLinkId()).isEqualTo("linkId123");
        assertThat(playback.getThumbnailImage()).isEqualTo("thumb.jpg");
        assertThat(playback.getDuration()).isEqualTo(Duration.fromString("3:30"));
    }

    @Test
    @DisplayName("create — endTime이 Duration 기반으로 계산된다")
    void createCalculatesEndTimeFromDuration() {
        // given
        long beforeEpoch = System.currentTimeMillis();

        // when
        PlaybackData playback = PlaybackData.create(
                new PartyroomId(1L), new UserId(10L),
                "Song", "5:00", "link1", "thumb.jpg"
        );

        // then
        long expectedMinEndTime = beforeEpoch + (5 * 60 * 1000);
        assertThat(playback.getEndTime()).isGreaterThanOrEqualTo(expectedMinEndTime);
        assertThat(playback.getEndTime()).isLessThan(expectedMinEndTime + 5000);
    }
}
