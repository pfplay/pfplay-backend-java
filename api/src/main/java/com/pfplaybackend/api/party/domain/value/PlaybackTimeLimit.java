package com.pfplaybackend.api.party.domain.value;

import com.pfplaybackend.api.common.domain.value.Duration;

import java.io.Serializable;
import java.util.Objects;

public class PlaybackTimeLimit implements Serializable {

    private final int limitMinutes;

    private PlaybackTimeLimit(int limitMinutes) {
        this.limitMinutes = limitMinutes;
    }

    public static PlaybackTimeLimit ofMinutes(int minutes) {
        return new PlaybackTimeLimit(minutes);
    }

    public static PlaybackTimeLimit unlimited() {
        return new PlaybackTimeLimit(0);
    }

    public boolean isUnlimited() {
        return limitMinutes <= 0;
    }

    public boolean exceedsDuration(Duration duration) {
        return !isUnlimited() && duration.toSeconds() > limitMinutes * 60L;
    }

    public int getMinutes() {
        return limitMinutes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaybackTimeLimit that = (PlaybackTimeLimit) o;
        return limitMinutes == that.limitMinutes;
    }

    @Override
    public int hashCode() {
        return Objects.hash(limitMinutes);
    }

    @Override
    public String toString() {
        return isUnlimited() ? "unlimited" : limitMinutes + "min";
    }
}
