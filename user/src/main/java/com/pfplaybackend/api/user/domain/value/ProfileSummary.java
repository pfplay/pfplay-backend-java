package com.pfplaybackend.api.user.domain.value;

import com.pfplaybackend.api.common.domain.enums.AvatarCompositionType;

import java.util.List;

public record ProfileSummary(
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
        List<ActivitySummary> activitySummaries
) {}
