package com.pfplaybackend.api.partyroom.presentation.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PartyRoomCreateAdminInfo {
    private final String profile;
    private final String userName;

    public PartyRoomCreateAdminInfo(String profile, String userName) {
        this.profile = profile;
        this.userName = userName;
    }
}
