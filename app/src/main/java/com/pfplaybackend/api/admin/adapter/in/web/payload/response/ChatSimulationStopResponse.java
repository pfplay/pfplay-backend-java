package com.pfplaybackend.api.admin.adapter.in.web.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채팅 시뮬레이션 중지 응답")
public record ChatSimulationStopResponse(
        @Schema(description = "파티룸 ID") Long partyroomId,
        @Schema(description = "시뮬레이션 상태") String status
) {}
