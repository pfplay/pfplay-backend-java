package com.pfplaybackend.api.partyroom.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum PartyroomPenaltyType {
    DELETE("delete"),
    GGUL("ggul"),
    KICK("kick"),
    BAN("ban");

    private final String name;
    PartyroomPenaltyType(String name){
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    @JsonCreator
    public static PartyroomPenaltyType fromValue(String value) {
        for (PartyroomPenaltyType penaltyType: PartyroomPenaltyType.values()) {
            if (penaltyType.name.equalsIgnoreCase(value)) {
                return penaltyType;
            }
        }

        throw new IllegalArgumentException("Unknown Partyroom Penalty Type, " + value);
    }
}
