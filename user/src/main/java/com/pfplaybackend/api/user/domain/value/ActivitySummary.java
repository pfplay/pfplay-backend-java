package com.pfplaybackend.api.user.domain.value;

import com.pfplaybackend.api.user.domain.enums.ActivityType;

public record ActivitySummary(ActivityType activityType, int score) {}
