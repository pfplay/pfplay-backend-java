package com.pfplaybackend.api.party.application.dto.result;

import com.pfplaybackend.api.party.application.dto.dj.DjWithProfileDto;
import io.swagger.v3.oas.annotations.media.Schema;

public record PartyroomSummaryResult(
        @Schema(example = "Friday Night Party") String title,
        @Schema(example = "Welcome to the party!") String introduction,
        @Schema(example = "abc123") String linkDomain,
        @Schema(example = "300") int playbackTimeLimit,
        DjWithProfileDto currentDj
) {}
