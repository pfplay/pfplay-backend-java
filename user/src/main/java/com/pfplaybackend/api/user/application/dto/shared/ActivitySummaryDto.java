package com.pfplaybackend.api.user.application.dto.shared;

import com.pfplaybackend.api.user.domain.enums.ActivityType;

public record ActivitySummaryDto(ActivityType activityType, int score) {}
