package com.pfplaybackend.api.common.domain.value;

import java.io.Serializable;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class Duration implements Serializable {

    private final long totalSeconds;

    private Duration(long totalSeconds) {
        if (totalSeconds < 0) throw new IllegalArgumentException("Duration cannot be negative");
        this.totalSeconds = totalSeconds;
    }

    public static Duration fromString(String durationStr) {
        if (durationStr == null || durationStr.isBlank()) {
            throw new DateTimeParseException("Duration string is null or blank", "", 0);
        }
        String[] parts = durationStr.split(":");
        return switch (parts.length) {
            case 2 -> {
                int minutes = Integer.parseInt(parts[0]);
                int seconds = Integer.parseInt(parts[1]);
                yield new Duration(minutes * 60L + seconds);
            }
            case 3 -> {
                int hours = Integer.parseInt(parts[0]);
                int minutes = Integer.parseInt(parts[1]);
                int seconds = Integer.parseInt(parts[2]);
                yield new Duration(hours * 3600L + minutes * 60L + seconds);
            }
            default -> throw new DateTimeParseException("Invalid duration format", durationStr, 0);
        };
    }

    public static Duration ofSeconds(long seconds) {
        return new Duration(seconds);
    }

    public long toSeconds() {
        return totalSeconds;
    }

    public boolean exceeds(Duration other) {
        return this.totalSeconds > other.totalSeconds;
    }

    public long calculateEndTimeEpochMilli(Instant now) {
        return now.plusSeconds(totalSeconds).toEpochMilli();
    }

    public long calculateEndTimeEpochMilli() {
        return calculateEndTimeEpochMilli(Instant.now());
    }

    public String toDisplayString() {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Duration duration = (Duration) o;
        return totalSeconds == duration.totalSeconds;
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalSeconds);
    }

    @Override
    public String toString() {
        return toDisplayString();
    }
}
