package com.pfplaybackend.api.party.application.dto.command;

import com.pfplaybackend.api.common.domain.enums.AvatarCompositionType;
import com.pfplaybackend.api.party.domain.value.PartyroomId;

public record CrewProfileChangedCommand(
        PartyroomId partyroomId,
        long crewId,
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
) implements java.io.Serializable {}
