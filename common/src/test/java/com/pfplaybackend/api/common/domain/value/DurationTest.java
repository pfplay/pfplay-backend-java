package com.pfplaybackend.api.common.domain.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.format.DateTimeParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DurationTest {

    private static final String DURATION_3_45 = "3:45";
    private static final String DURATION_1_23_45 = "1:23:45";

    @Test
    @DisplayName("MM:SS 포맷에서 Duration 생성")
    void fromStringMmss() {
        Duration duration = Duration.fromString(DURATION_3_45);
        assertThat(duration.toSeconds()).isEqualTo(225L);
    }

    @Test
    @DisplayName("H:MM:SS 포맷에서 Duration 생성")
    void fromStringHmmss() {
        Duration duration = Duration.fromString(DURATION_1_23_45);
        assertThat(duration.toSeconds()).isEqualTo(5025L);
    }

    @Test
    @DisplayName("0:00 포맷도 유효")
    void fromStringZero() {
        Duration duration = Duration.fromString("0:00");
        assertThat(duration.toSeconds()).isZero();
    }

    @Test
    @DisplayName("toDisplayString은 원래 포맷으로 복원")
    void toDisplayStringMmss() {
        Duration duration = Duration.fromString(DURATION_3_45);
        assertThat(duration.toDisplayString()).isEqualTo(DURATION_3_45);
    }

    @Test
    @DisplayName("toDisplayString은 시간 포맷을 보존")
    void toDisplayStringHmmss() {
        Duration duration = Duration.fromString(DURATION_1_23_45);
        assertThat(duration.toDisplayString()).isEqualTo(DURATION_1_23_45);
    }

    @Test
    @DisplayName("exceeds 비교가 올바르게 동작")
    void exceeds() {
        Duration longer = Duration.fromString("5:00");
        Duration shorter = Duration.fromString("3:00");
        assertThat(longer.exceeds(shorter)).isTrue();
        assertThat(shorter.exceeds(longer)).isFalse();
        assertThat(longer.exceeds(longer)).isFalse();
    }

    @Test
    @DisplayName("ofSeconds 정적 팩토리")
    void ofSeconds() {
        Duration duration = Duration.ofSeconds(180);
        assertThat(duration.toSeconds()).isEqualTo(180L);
        assertThat(duration.toDisplayString()).isEqualTo("3:00");
    }

    @Test
    @DisplayName("잘못된 포맷은 예외 발생")
    void fromStringInvalidFormat() {
        assertThatThrownBy(() -> Duration.fromString("invalid"))
                .isInstanceOf(DateTimeParseException.class);
    }

    @Test
    @DisplayName("null 입력은 예외 발생")
    void fromStringNull() {
        assertThatThrownBy(() -> Duration.fromString(null))
                .isInstanceOf(DateTimeParseException.class);
    }

    @Test
    @DisplayName("음수 초는 예외 발생")
    void ofSecondsNegative() {
        assertThatThrownBy(() -> Duration.ofSeconds(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("equals와 hashCode 동작")
    void equalsAndHashCode() {
        Duration d1 = Duration.fromString(DURATION_3_45);
        Duration d2 = Duration.fromString(DURATION_3_45);
        Duration d3 = Duration.fromString("4:00");

        assertThat(d1).isEqualTo(d2)
                .isNotEqualTo(d3)
                .hasSameHashCodeAs(d2);
    }
}
