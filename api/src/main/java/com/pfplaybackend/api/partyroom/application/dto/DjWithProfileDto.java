package com.pfplaybackend.api.partyroom.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DjWithProfileDto {
    private long djId;
    private long orderNumber;
    private String nickname;
    private String avatarIconUri;
}
