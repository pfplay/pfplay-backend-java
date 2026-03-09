package com.pfplaybackend.api.party.adapter.in.web.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "패널티 부과 응답")
public record CreatePenaltyResponse(
        @Schema(description = "생성된 패널티 ID") Long penaltyId
) {}
