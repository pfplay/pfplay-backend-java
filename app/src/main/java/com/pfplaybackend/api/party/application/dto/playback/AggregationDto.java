package com.pfplaybackend.api.party.application.dto.playback;

import io.swagger.v3.oas.annotations.media.Schema;

public record AggregationDto(
        @Schema(example = "10") int likeCount,
        @Schema(example = "2") int dislikeCount,
        @Schema(example = "5") int grabCount
) {}
