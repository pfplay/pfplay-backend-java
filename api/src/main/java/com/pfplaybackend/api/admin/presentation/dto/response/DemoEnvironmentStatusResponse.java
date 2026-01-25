package com.pfplaybackend.api.admin.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DemoEnvironmentStatusResponse {
    private Boolean initialized;
    private Long virtualMemberCount;
    private Long generalRoomCount;
}
