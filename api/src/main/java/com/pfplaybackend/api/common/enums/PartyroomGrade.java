package com.pfplaybackend.api.common.enums;

import lombok.Getter;

public enum PartyroomGrade {
    ADMIN("admin", 1),
    CM("cm", 2),
    MOD("mod", 3),
    CLUBBER("clubber", 4),
    LISTENER("listener", 5);

    @Getter
    private final String name;

    @Getter
    private final int priority;

    PartyroomGrade(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }
}
