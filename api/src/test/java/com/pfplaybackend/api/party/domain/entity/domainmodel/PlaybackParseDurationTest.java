package com.pfplaybackend.api.party.domain.entity.domainmodel;

import com.pfplaybackend.api.party.domain.entity.data.PlaybackData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.format.DateTimeParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlaybackParseDurationTest {

    private Duration invokeParseDuration(String durationStr) throws Exception {
        Method method = PlaybackData.class.getDeclaredMethod("parseDuration", String.class);
        method.setAccessible(true);
        return (Duration) method.invoke(null, durationStr);
    }

    @Test
    @DisplayName("MM:SS 포맷 파싱 - 3:45")
    void parseDuration_mmss() throws Exception {
        Duration result = invokeParseDuration("3:45");
        assertThat(result).isEqualTo(Duration.ofMinutes(3).plusSeconds(45));
    }

    @Test
    @DisplayName("H:MM:SS 포맷 파싱 - 1:23:45")
    void parseDuration_hmmss() throws Exception {
        Duration result = invokeParseDuration("1:23:45");
        assertThat(result).isEqualTo(Duration.ofHours(1).plusMinutes(23).plusSeconds(45));
    }

    @Test
    @DisplayName("H:MM:SS 포맷 파싱 - 0:05:30")
    void parseDuration_hmmss_zeroHour() throws Exception {
        Duration result = invokeParseDuration("0:05:30");
        assertThat(result).isEqualTo(Duration.ofMinutes(5).plusSeconds(30));
    }

    @Test
    @DisplayName("잘못된 포맷은 DateTimeParseException 발생")
    void parseDuration_invalidFormat() {
        assertThatThrownBy(() -> invokeParseDuration("invalid"))
                .hasCauseInstanceOf(DateTimeParseException.class);
    }
}
