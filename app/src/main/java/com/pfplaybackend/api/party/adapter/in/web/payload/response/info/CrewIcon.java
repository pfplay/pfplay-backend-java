package com.pfplaybackend.api.party.adapter.in.web.payload.response.info;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "크루 아이콘 정보")
public record CrewIcon(
        @Schema(description = "아바타 아이콘 URI") String avatarIconUri
) {}
