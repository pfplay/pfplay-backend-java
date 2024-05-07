package com.pfplaybackend.api.partyroom.enums;

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
}
