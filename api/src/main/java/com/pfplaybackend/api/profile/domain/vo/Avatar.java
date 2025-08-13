package com.pfplaybackend.api.profile.domain.vo;

import com.pfplaybackend.api.profile.domain.enums.AvatarCompositionType;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.ValidationException;

import java.util.Objects;
import java.util.Optional;

public record Avatar(
        AvatarCompositionType compositionType,
        AvatarBody body,
        AvatarFace face
) {

    public Avatar {
        Objects.requireNonNull(compositionType, "Composition type은 필수입니다.");
        Objects.requireNonNull(body, "Body는 필수입니다.");
        validateComposition(compositionType, face);
    }

    public static Avatar singleBody(AvatarBody body) {
        return new Avatar(AvatarCompositionType.SINGLE_BODY, body, null);
    }

    public static Avatar bodyWithFace(AvatarBody body, AvatarFace face) {
        Objects.requireNonNull(face, "BODY_WITH_FACE 타입에서는 Face가 필수입니다.");
        return new Avatar(AvatarCompositionType.BODY_WITH_FACE, body, face);
    }

    /**
     * Avatar 유효성 검증
     */
    public boolean isValid() {
        boolean bodyValid = body != null && body.isValid();
        boolean faceValid = face == null || face.isValid();
        return bodyValid && faceValid;
    }

    public boolean isSingleBody() {
        return compositionType == AvatarCompositionType.SINGLE_BODY;
    }

    public boolean isBodyWithFace() {
        return compositionType == AvatarCompositionType.BODY_WITH_FACE;
    }

    public Optional<AvatarFace> getFace() {
        return Optional.ofNullable(face);
    }

    private static void validateComposition(AvatarCompositionType compositionType, AvatarFace face) {
        switch (compositionType) {
            case SINGLE_BODY -> {
                if (face != null) {
                    throw new ValidationException("SINGLE_BODY 타입에서는 face 정보가 없어야 합니다.");
                }
            }
            case BODY_WITH_FACE -> {
                if (face == null) {
                    throw new ValidationException("BODY_WITH_FACE 타입에서는 face 정보가 필수입니다.");
                }
            }
        }
    }
}
