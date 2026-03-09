package com.pfplaybackend.api.party.adapter.in.web.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DJ 등록 응답")
public record CreateDjResponse(
        @Schema(description = "생성된 DJ ID") Long djId
) {}
