package com.pfplaybackend.api.partyroom.enums;

import lombok.Getter;

@Getter
public enum PartyroomGrade {
    ADMIN("admin", 1),
    CM("cm", 2),
    MOD("mod", 3),
    CLUBBER("clubber", 4),
    LISTENER("listener", 5);

    private final String name;
    private final int priority;

    PartyroomGrade(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }
}
