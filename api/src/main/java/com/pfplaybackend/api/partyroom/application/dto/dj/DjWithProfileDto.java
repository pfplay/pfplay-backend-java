package com.pfplaybackend.api.partyroom.application.dto.dj;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DjWithProfileDto {
    // FIXME Change to crewId
    private long crewId;
    private long orderNumber;
    private String nickname;
    private String avatarIconUri;
}
