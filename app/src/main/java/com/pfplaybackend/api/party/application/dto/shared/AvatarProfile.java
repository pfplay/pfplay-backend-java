package com.pfplaybackend.api.party.application.dto.shared;

import com.pfplaybackend.api.common.domain.enums.AvatarCompositionType;

import java.io.Serializable;

public record AvatarProfile(
        AvatarCompositionType avatarCompositionType,
        String avatarBodyUri,
        String avatarFaceUri,
        String avatarIconUri,
        int combinePositionX,
        int combinePositionY,
        double offsetX,
        double offsetY,
        double scale
) implements Serializable {

    public static AvatarProfile from(
            AvatarCompositionType type, String bodyUri, String faceUri, String iconUri,
            int posX, int posY, double offX, double offY, double scale) {
        return new AvatarProfile(type, bodyUri, faceUri, iconUri, posX, posY, offX, offY, scale);
    }
}
