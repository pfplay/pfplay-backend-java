package com.pfplaybackend.api.party.domain.entity.domainmodel;

import com.pfplaybackend.api.common.domain.value.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.format.DateTimeParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlaybackParseDurationTest {

    @Test
    @DisplayName("MM:SS 포맷 파싱 - 3:45")
    void parseDurationMmss() {
        Duration result = Duration.fromString("3:45");
        assertThat(result.toSeconds()).isEqualTo(3 * 60 + 45);
    }

    @Test
    @DisplayName("H:MM:SS 포맷 파싱 - 1:23:45")
    void parseDurationHmmss() {
        Duration result = Duration.fromString("1:23:45");
        assertThat(result.toSeconds()).isEqualTo(1 * 3600 + 23 * 60 + 45);
    }

    @Test
    @DisplayName("H:MM:SS 포맷 파싱 - 0:05:30")
    void parseDurationHmmssZeroHour() {
        Duration result = Duration.fromString("0:05:30");
        assertThat(result.toSeconds()).isEqualTo(5 * 60 + 30);
    }

    @Test
    @DisplayName("잘못된 포맷은 DateTimeParseException 발생")
    void parseDurationInvalidFormat() {
        assertThatThrownBy(() -> Duration.fromString("invalid"))
                .isInstanceOf(DateTimeParseException.class);
    }
}
