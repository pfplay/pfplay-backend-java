package com.pfplaybackend.api.playlist.adapter.in.web.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "트랙 추가 응답")
public record CreateTrackResponse(
        @Schema(description = "생성된 트랙 ID") Long trackId
) {}
