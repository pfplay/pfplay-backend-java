package com.pfplaybackend.api.party.application.dto.dj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DjWithProfileDto {
    // FIXME Change to crewId
    private long crewId;
    private long orderNumber;
    private String nickname;
    private String avatarIconUri;
}
