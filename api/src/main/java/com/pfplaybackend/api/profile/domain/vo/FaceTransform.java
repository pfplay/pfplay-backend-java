package com.pfplaybackend.api.profile.domain.vo;

import jakarta.validation.ValidationException;

public record FaceTransform(
        double offsetX,
        double offsetY,
        double scale
) {

    public FaceTransform {
        validateScale(scale);
        validateOffsets(offsetX, offsetY);
    }

    public static FaceTransform of(double offsetX, double offsetY, double scale) {
        return new FaceTransform(offsetX, offsetY, scale);
    }

    /**
     * Transform 유효성 검증
     */
    public boolean isValid() {
        return scale >= 0 && scale <= 200 &&
                Math.abs(offsetX) <= 1000 && Math.abs(offsetY) <= 1000;
    }

    private static void validateScale(double scale) {
        if (scale < 0 || scale > 200) {
            throw new ValidationException("Scale 값은 0~200 범위여야 합니다. 입력값: " + scale);
        }
    }

    private static void validateOffsets(double offsetX, double offsetY) {
        if (Math.abs(offsetX) > 1000 || Math.abs(offsetY) > 1000) {
            throw new ValidationException("Offset 값은 -1000~1000 범위여야 합니다.");
        }
    }
}
