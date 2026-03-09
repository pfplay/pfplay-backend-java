package com.pfplaybackend.api.party.adapter.in.web.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "크루 차단 응답")
public record CreateBlockResponse(
        @Schema(description = "생성된 차단 ID") Long blockId
) {}
