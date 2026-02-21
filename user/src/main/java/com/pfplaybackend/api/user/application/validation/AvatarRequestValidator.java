package com.pfplaybackend.api.user.application.validation;

import com.pfplaybackend.api.common.domain.enums.AvatarCompositionType;
import com.pfplaybackend.api.user.application.dto.command.SetAvatarCommand;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AvatarRequestValidator {

    public void validate(SetAvatarCommand command) {
        log.debug("아바타 요청 검증 시작 - 요청: {}", command);

        validateCompositionTypeAndFace(command);
        validateUriFormat(command);
        validateBusinessRules(command);

        log.debug("아바타 요청 검증 완료");
    }

    private void validateCompositionTypeAndFace(SetAvatarCommand command) {
        AvatarCompositionType type = command.avatarCompositionType();

        switch (type) {
            case SINGLE_BODY -> {
                if (command.face() != null) {
                    throw new ValidationException("SINGLE_BODY 타입에서는 face 정보가 없어야 합니다.");
                }
            }
            case BODY_WITH_FACE -> {
                if (command.face() == null) {
                    throw new ValidationException("BODY_WITH_FACE 타입에서는 face 정보가 필수입니다.");
                }
            }
        }
    }

    private void validateUriFormat(SetAvatarCommand command) {
        // Body URI 검증
        String bodyUri = command.body().uri();
        if (!isValidBodyUri(bodyUri)) {
            throw new ValidationException("유효하지 않은 body URI 형식입니다: " + bodyUri);
        }

        // Face URI 검증 (존재할 경우)
        if (command.face() != null) {
            String faceUri = command.face().uri();
            if (!isValidFaceUri(faceUri, command.face())) {
                throw new ValidationException("유효하지 않은 face URI 형식입니다: " + faceUri);
            }
        }
    }

    private void validateBusinessRules(SetAvatarCommand command) {
        // Transform 값 검증
        if (command.face() != null) {
            var transform = command.face().transform();

            // Scale 범위 재검증
            if (transform.scale() < 0 || transform.scale() > 200) {
                throw new ValidationException("Scale 값은 0-200 범위여야 합니다: " + transform.scale());
            }

            // Offset 극값 검증
            if (Math.abs(transform.offsetX()) > 1000 || Math.abs(transform.offsetY()) > 1000) {
                throw new ValidationException("Offset 값이 허용 범위를 초과했습니다.");
            }
        }
    }

    private boolean isValidBodyUri(String uri) {
        return uri != null && uri.startsWith("https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/");
    }

    private boolean isValidFaceUri(String uri, SetAvatarCommand.AvatarFaceSpec face) {
        if (uri == null) return false;

        return switch (face.sourceType()) {
            case INTERNAL_IMAGE -> uri.startsWith("https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/");
            case NFT_URI -> uri.startsWith("http://") || uri.startsWith("https://");
        };
    }
}
