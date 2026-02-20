package com.pfplaybackend.api.party.adapter.in.listener.message;

import com.pfplaybackend.api.common.domain.enums.AvatarCompositionType;
import com.pfplaybackend.api.common.domain.value.UserId;

import java.io.Serializable;

public record CrewProfilePreCheckMessage(
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
) implements Serializable {}
