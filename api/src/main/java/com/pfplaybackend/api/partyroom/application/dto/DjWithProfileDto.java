package com.pfplaybackend.api.partyroom.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DjWithProfileDto {
    // FIXME Change to crewId
    private long djId;
    private long orderNumber;
    private String nickname;
    private String avatarIconUri;
}
