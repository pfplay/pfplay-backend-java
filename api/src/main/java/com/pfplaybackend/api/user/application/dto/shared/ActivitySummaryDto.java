package com.pfplaybackend.api.user.application.dto.shared;

import com.pfplaybackend.api.user.domain.model.enums.ActivityType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActivitySummaryDto {
    private ActivityType activityType;
    private int score;
}
