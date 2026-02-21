package com.pfplaybackend.api.admin.adapter.in.web.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class QueryDemoEnvironmentStatusResponse {
    private Boolean initialized;
    private Long virtualMemberCount;
    private Long generalRoomCount;
}
