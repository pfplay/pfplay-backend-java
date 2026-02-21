package com.pfplaybackend.api.party.application.dto.command;

import com.pfplaybackend.api.common.domain.enums.AvatarCompositionType;
import com.pfplaybackend.api.common.domain.value.UserId;

public record CrewProfilePreCheckCommand(
        UserId userId,
        String nickname,
        String avatarFaceUri,
        String avatarBodyUri,
        String avatarIconUri,
        AvatarCompositionType avatarCompositionType,
        int combinePositionX,
        int combinePositionY,
        double offsetX,
        double offsetY,
        double scale
) {}
