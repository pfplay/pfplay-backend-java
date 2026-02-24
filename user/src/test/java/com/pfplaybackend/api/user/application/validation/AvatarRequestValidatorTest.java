package com.pfplaybackend.api.user.application.validation;

import com.pfplaybackend.api.common.domain.enums.AvatarCompositionType;
import com.pfplaybackend.api.user.application.dto.command.SetAvatarCommand;
import com.pfplaybackend.api.user.domain.enums.FaceSourceType;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AvatarRequestValidatorTest {

    private final AvatarRequestValidator validator = new AvatarRequestValidator();

    private static final String VALID_BODY_URI =
            "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/test.png?alt=media";
    private static final String VALID_FACE_URI =
            "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/face.png?alt=media";

    @Test
    @DisplayName("validate — SINGLE_BODY 타입에서 face가 있으면 ValidationException이 발생한다")
    void validateSingleBodyWithFaceThrowsException() {
        // given
        SetAvatarCommand command = new SetAvatarCommand(
                AvatarCompositionType.SINGLE_BODY,
                new SetAvatarCommand.AvatarBodySpec(VALID_BODY_URI),
                new SetAvatarCommand.AvatarFaceSpec(
                        VALID_FACE_URI,
                        FaceSourceType.INTERNAL_IMAGE,
                        new SetAvatarCommand.AvatarTransformSpec(0, 0, 100)
                )
        );

        // when & then
        assertThatThrownBy(() -> validator.validate(command))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("validate — BODY_WITH_FACE 타입에서 face가 없으면 ValidationException이 발생한다")
    void validateBodyWithFaceWithoutFaceThrowsException() {
        // given
        SetAvatarCommand command = new SetAvatarCommand(
                AvatarCompositionType.BODY_WITH_FACE,
                new SetAvatarCommand.AvatarBodySpec(VALID_BODY_URI),
                null
        );

        // when & then
        assertThatThrownBy(() -> validator.validate(command))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("validate — 유효하지 않은 body URI이면 ValidationException이 발생한다")
    void validateInvalidBodyUriThrowsException() {
        // given
        SetAvatarCommand command = new SetAvatarCommand(
                AvatarCompositionType.SINGLE_BODY,
                new SetAvatarCommand.AvatarBodySpec("https://invalid-domain.com/body.png"),
                null
        );

        // when & then
        assertThatThrownBy(() -> validator.validate(command))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("validate — scale이 200을 초과하면 ValidationException이 발생한다")
    void validateScaleExceeds200ThrowsException() {
        // given
        SetAvatarCommand command = new SetAvatarCommand(
                AvatarCompositionType.BODY_WITH_FACE,
                new SetAvatarCommand.AvatarBodySpec(VALID_BODY_URI),
                new SetAvatarCommand.AvatarFaceSpec(
                        VALID_FACE_URI,
                        FaceSourceType.INTERNAL_IMAGE,
                        new SetAvatarCommand.AvatarTransformSpec(0, 0, 250)
                )
        );

        // when & then
        assertThatThrownBy(() -> validator.validate(command))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("validate — 유효한 SINGLE_BODY 요청이면 예외가 발생하지 않는다")
    void validateValidSingleBodyNoException() {
        // given
        SetAvatarCommand command = new SetAvatarCommand(
                AvatarCompositionType.SINGLE_BODY,
                new SetAvatarCommand.AvatarBodySpec(VALID_BODY_URI),
                null
        );

        // when & then
        assertThatCode(() -> validator.validate(command))
                .doesNotThrowAnyException();
    }
}
