package com.pfplaybackend.api.party.application.dto.dj;

import io.swagger.v3.oas.annotations.media.Schema;

public record DjWithProfileDto(
        @Schema(example = "1") long crewId,
        @Schema(example = "1") long orderNumber,
        @Schema(example = "DJ_Master") String nickname,
        @Schema(example = "https://cdn.pfplay.xyz/avatar/icon/1.png") String avatarIconUri
) {}
