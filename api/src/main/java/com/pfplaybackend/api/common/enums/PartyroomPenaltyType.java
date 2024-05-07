package com.pfplaybackend.api.common.enums;

import lombok.Getter;

public enum PartyroomPenaltyType {
    DELETE("delete"),
    GGUL("ggul"),
    KICK("kick"),
    BAN("ban");


    @Getter
    private String name;
    PartyroomPenaltyType(String name){
        this.name = name;
    }
}
