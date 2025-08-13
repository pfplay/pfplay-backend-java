package com.pfplaybackend.api.profile.domain.vo;

import jakarta.validation.ValidationException;

public record AvatarBody(
        String uri
) {

    public AvatarBody {
        validateUri(uri);
    }

    public static AvatarBody of(String uri) {
        return new AvatarBody(uri);
    }

    /**
     * Body 유효성 검증
     */
    public boolean isValid() {
        return uri != null && !uri.trim().isEmpty() && uri.startsWith("pfplay://avatar/body/");
    }

    private static void validateUri(String uri) {
        if (uri == null || uri.trim().isEmpty()) {
            throw new ValidationException("Body URI는 필수입니다.");
        }
        if (!uri.startsWith("pfplay://avatar/body/")) {
            throw new ValidationException("올바른 Body URI 형식이 아닙니다: " + uri);
        }
    }
}
