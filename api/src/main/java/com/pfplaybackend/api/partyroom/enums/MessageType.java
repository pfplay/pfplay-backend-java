package com.pfplaybackend.api.partyroom.enums;

import lombok.Getter;

@Getter
public enum MessageType {
    CHAT("chat"),
    PROMOTE("promote"),
    PENALTY("penalty");

    private final String name;

    MessageType(String name) {
        this.name = name;
    }
}
