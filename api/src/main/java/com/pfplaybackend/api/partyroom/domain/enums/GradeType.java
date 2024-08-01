package com.pfplaybackend.api.partyroom.domain.enums;

public enum GradeType {
    HOST(5),
    COMMUNITY_MANAGER(4),
    MODERATOR(3),
    CLUBBER(2),
    LISTENER(1);

    private final int level;

    GradeType(int level) {
        this.level = level;
    }

    public boolean isEqualOrHigherThan(GradeType other) {
        return this.level >= other.level;
    }

    public boolean isHigherThan(GradeType other) {
        return this.level > other.level;
    }

    public boolean isLowerThan(GradeType other) {
        return this.level < other.level;
    }
}