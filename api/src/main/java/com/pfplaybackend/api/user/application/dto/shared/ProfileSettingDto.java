package com.pfplaybackend.api.user.application.dto.shared;

import com.pfplaybackend.api.common.domain.enums.AvatarCompositionType;

public record ProfileSettingDto(
        String nickname,
        AvatarCompositionType avatarCompositionType,
        String avatarBodyUri,
        String avatarFaceUri,
        String avatarIconUri,
        int combinePositionX,
        int combinePositionY,
        double offsetX,
        double offsetY,
        double scale
) {}
