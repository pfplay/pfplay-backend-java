package com.pfplaybackend.api.party.domain.value;

import com.pfplaybackend.api.common.domain.value.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlaybackTimeLimitTest {

    @Test
    @DisplayName("unlimited는 제한 없음")
    void unlimited() {
        PlaybackTimeLimit limit = PlaybackTimeLimit.unlimited();
        assertThat(limit.isUnlimited()).isTrue();
        assertThat(limit.getMinutes()).isEqualTo(0);
    }

    @Test
    @DisplayName("0분 제한은 unlimited와 동일")
    void zeroMinutesIsUnlimited() {
        PlaybackTimeLimit limit = PlaybackTimeLimit.ofMinutes(0);
        assertThat(limit.isUnlimited()).isTrue();
    }

    @Test
    @DisplayName("음수 분 제한은 unlimited와 동일")
    void negativeMinutesIsUnlimited() {
        PlaybackTimeLimit limit = PlaybackTimeLimit.ofMinutes(-1);
        assertThat(limit.isUnlimited()).isTrue();
    }

    @Test
    @DisplayName("양수 분 제한은 제한 있음")
    void positiveMinutesHasLimit() {
        PlaybackTimeLimit limit = PlaybackTimeLimit.ofMinutes(5);
        assertThat(limit.isUnlimited()).isFalse();
        assertThat(limit.getMinutes()).isEqualTo(5);
    }

    @Test
    @DisplayName("exceedsDuration — 제한 초과 감지")
    void exceedsDuration_exceeds() {
        PlaybackTimeLimit limit = PlaybackTimeLimit.ofMinutes(5);
        Duration longDuration = Duration.fromString("6:00"); // 360초 > 300초
        assertThat(limit.exceedsDuration(longDuration)).isTrue();
    }

    @Test
    @DisplayName("exceedsDuration — 제한 이내")
    void exceedsDuration_within() {
        PlaybackTimeLimit limit = PlaybackTimeLimit.ofMinutes(5);
        Duration shortDuration = Duration.fromString("4:30"); // 270초 < 300초
        assertThat(limit.exceedsDuration(shortDuration)).isFalse();
    }

    @Test
    @DisplayName("exceedsDuration — 정확히 같은 길이")
    void exceedsDuration_exact() {
        PlaybackTimeLimit limit = PlaybackTimeLimit.ofMinutes(5);
        Duration exactDuration = Duration.fromString("5:00"); // 300초 == 300초
        assertThat(limit.exceedsDuration(exactDuration)).isFalse();
    }

    @Test
    @DisplayName("unlimited는 어떤 Duration도 초과하지 않음")
    void unlimitedNeverExceeds() {
        PlaybackTimeLimit limit = PlaybackTimeLimit.unlimited();
        Duration longDuration = Duration.fromString("99:59");
        assertThat(limit.exceedsDuration(longDuration)).isFalse();
    }

    @Test
    @DisplayName("equals와 hashCode 동작")
    void equalsAndHashCode() {
        PlaybackTimeLimit l1 = PlaybackTimeLimit.ofMinutes(5);
        PlaybackTimeLimit l2 = PlaybackTimeLimit.ofMinutes(5);
        PlaybackTimeLimit l3 = PlaybackTimeLimit.ofMinutes(10);

        assertThat(l1).isEqualTo(l2);
        assertThat(l1).isNotEqualTo(l3);
        assertThat(l1.hashCode()).isEqualTo(l2.hashCode());
    }
}
