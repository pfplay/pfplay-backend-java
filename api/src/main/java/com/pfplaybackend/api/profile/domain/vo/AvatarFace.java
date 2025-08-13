package com.pfplaybackend.api.profile.domain.vo;

import com.pfplaybackend.api.profile.domain.enums.FaceSourceType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.ValidationException;

import java.util.Objects;

public record AvatarFace(
        FaceSourceType sourceType,
        String uri,
        FaceTransform transform
) {

    public AvatarFace {
        Objects.requireNonNull(sourceType, "Face source type은 필수입니다.");
        Objects.requireNonNull(transform, "Face transform은 필수입니다.");
        validateUri(uri);
        validateSourceTypeAndUri(sourceType, uri);
    }

    public static AvatarFace of(FaceSourceType sourceType, String uri, FaceTransform transform) {
        return new AvatarFace(sourceType, uri, transform);
    }

    /**
     * Face 유효성 검증
     */
    public boolean isValid() {
        return uri != null && !uri.trim().isEmpty() &&
                transform != null && transform.isValid() &&
                isUriValidForSourceType();
    }

    private boolean isUriValidForSourceType() {
        return switch (sourceType) {
            case INTERNAL_IMAGE -> uri.startsWith("pfplay://avatar/face/");
            case NFT_URI -> uri.startsWith("http://") || uri.startsWith("https://");
        };
    }

    private static void validateUri(String uri) {
        if (uri == null || uri.trim().isEmpty()) {
            throw new ValidationException("Face URI는 필수입니다.");
        }
    }

    private static void validateSourceTypeAndUri(FaceSourceType sourceType, String uri) {
        switch (sourceType) {
            case INTERNAL_IMAGE -> {
                if (!uri.startsWith("pfplay://avatar/face/")) {
                    throw new ValidationException("INTERNAL_IMAGE 타입은 pfplay://avatar/face/ 스킴을 사용해야 합니다.");
                }
            }
            case NFT_URI -> {
                if (!uri.startsWith("http://") && !uri.startsWith("https://")) {
                    throw new ValidationException("NFT_URI 타입은 http/https 스킴을 사용해야 합니다.");
                }
            }
        }
    }
}
