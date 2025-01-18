package com.pfplaybackend.api.party.application.dto.dj;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CurrentDjWithProfileDto {
    private long crewId;
    private String nickname;
    private String avatarIconUri;
}
