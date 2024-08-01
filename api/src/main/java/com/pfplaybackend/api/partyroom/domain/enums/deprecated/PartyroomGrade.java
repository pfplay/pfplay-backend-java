package com.pfplaybackend.api.partyroom.domain.enums.deprecated;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
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

    @JsonValue
    public String getName() {
        return name;
    }

    @JsonCreator
    public static PartyroomGrade fromValue(String value) {
        for (PartyroomGrade grade: PartyroomGrade.values()) {
            if (grade.name.equalsIgnoreCase(value)) {
                return grade;
            }
        }

        throw new IllegalArgumentException("Unknown Partyroom Grade, " + value);
    }
}
