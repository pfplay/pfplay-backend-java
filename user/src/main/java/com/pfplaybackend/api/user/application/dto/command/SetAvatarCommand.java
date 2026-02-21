package com.pfplaybackend.api.user.application.dto.command;

import com.pfplaybackend.api.common.domain.enums.AvatarCompositionType;
import com.pfplaybackend.api.user.domain.enums.FaceSourceType;

public record SetAvatarCommand(
        AvatarCompositionType avatarCompositionType,
        AvatarBodySpec body,
        AvatarFaceSpec face
) {
    public record AvatarBodySpec(String uri) {}

    public record AvatarFaceSpec(
            String uri,
            FaceSourceType sourceType,
            AvatarTransformSpec transform
    ) {}

    public record AvatarTransformSpec(double offsetX, double offsetY, double scale) {}
}
