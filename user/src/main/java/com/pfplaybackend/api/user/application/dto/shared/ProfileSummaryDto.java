package com.pfplaybackend.api.user.application.dto.shared;

import com.pfplaybackend.api.common.domain.enums.AvatarCompositionType;

import java.util.List;

public record ProfileSummaryDto(
        String nickname,
        String introduction,
        String avatarBodyUri,
        AvatarCompositionType avatarCompositionType,
        int combinePositionX,
        int combinePositionY,
        double offsetX,
        double offsetY,
        double scale,
        String avatarFaceUri,
        String avatarIconUri,
        String walletAddress,
        List<ActivitySummaryDto> activitySummaries
) {}
