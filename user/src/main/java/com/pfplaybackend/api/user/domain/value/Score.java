package com.pfplaybackend.api.user.domain.value;

import java.io.Serializable;
import java.util.Objects;

public class Score implements Serializable {

    private final int value;

    public Score(int value) {
        this.value = Math.max(0, value);
    }

    public static Score zero() {
        return new Score(0);
    }

    public Score add(int delta) {
        return new Score(this.value + delta);
    }

    public boolean isAtLeast(int threshold) {
        return value >= threshold;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Score score = (Score) o;
        return value == score.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
