package com.pfplaybackend.api.profile.application.validation;

import com.pfplaybackend.api.profile.domain.enums.AvatarCompositionType;
import com.pfplaybackend.api.profile.presentation.dto.request.AvatarFaceRequest;
import com.pfplaybackend.api.profile.presentation.dto.request.SetAvatarRequest;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AvatarRequestValidator {

    public void validate(SetAvatarRequest request) {
        log.debug("아바타 요청 검증 시작 - 요청: {}", request);

        validateCompositionTypeAndFace(request);
        validateUriFormat(request);
        validateBusinessRules(request);

        log.debug("아바타 요청 검증 완료");
    }

    private void validateCompositionTypeAndFace(SetAvatarRequest request) {
        AvatarCompositionType type = request.getAvatarCompositionType();

        switch (type) {
            case SINGLE_BODY -> {
                if (request.getFace() != null) {
                    throw new ValidationException("SINGLE_BODY 타입에서는 face 정보가 없어야 합니다.");
                }
            }
            case BODY_WITH_FACE -> {
                if (request.getFace() == null) {
                    throw new ValidationException("BODY_WITH_FACE 타입에서는 face 정보가 필수입니다.");
                }
            }
        }
    }

    private void validateUriFormat(SetAvatarRequest request) {
        // Body URI 검증
        String bodyUri = request.getBody().getUri();
        if (!isValidBodyUri(bodyUri)) {
            throw new ValidationException("유효하지 않은 body URI 형식입니다: " + bodyUri);
        }

        // Face URI 검증 (존재할 경우)
        if (request.getFace() != null) {
            String faceUri = request.getFace().getUri();
            if (!isValidFaceUri(faceUri, request.getFace())) {
                throw new ValidationException("유효하지 않은 face URI 형식입니다: " + faceUri);
            }
        }
    }

    private void validateBusinessRules(SetAvatarRequest request) {
        // Transform 값 검증
        if (request.getFace() != null) {
            var transform = request.getFace().getTransform();

            // Scale 범위 재검증
            if (transform.getScale() < 0 || transform.getScale() > 200) {
                throw new ValidationException("Scale 값은 0-200 범위여야 합니다: " + transform.getScale());
            }

            // Offset 극값 검증
            if (Math.abs(transform.getOffsetX()) > 1000 || Math.abs(transform.getOffsetY()) > 1000) {
                throw new ValidationException("Offset 값이 허용 범위를 초과했습니다.");
            }
        }
    }

    private boolean isValidBodyUri(String uri) {
        return uri != null && uri.startsWith("https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/");
    }

    private boolean isValidFaceUri(String uri, AvatarFaceRequest face) {
        if (uri == null) return false;

        return switch (face.getSourceType()) {
            case INTERNAL_IMAGE -> uri.startsWith("https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/");
            case NFT_URI -> uri.startsWith("http://") || uri.startsWith("https://");
        };
    }
}
