package com.pfplaybackend.api.user.application.dto.shared;

import com.pfplaybackend.api.user.domain.enums.ActivityType;
import io.swagger.v3.oas.annotations.media.Schema;

public record ActivitySummaryDto(
        @Schema(example = "DJ_POINT") ActivityType activityType,
        @Schema(example = "150") int score
) {}
