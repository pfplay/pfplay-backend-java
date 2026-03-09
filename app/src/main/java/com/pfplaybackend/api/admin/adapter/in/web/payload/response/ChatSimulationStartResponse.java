package com.pfplaybackend.api.admin.adapter.in.web.payload.response;

import com.pfplaybackend.api.admin.domain.enums.ChatScriptType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채팅 시뮬레이션 시작 응답")
public record ChatSimulationStartResponse(
        @Schema(description = "파티룸 ID") Long partyroomId,
        @Schema(description = "시뮬레이션 상태") String status,
        @Schema(description = "스크립트 타입") ChatScriptType scriptType
) {}
