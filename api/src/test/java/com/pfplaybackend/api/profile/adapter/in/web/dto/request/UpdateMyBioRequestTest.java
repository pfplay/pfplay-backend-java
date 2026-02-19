package com.pfplaybackend.api.profile.adapter.in.web.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateMyBioRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private UpdateMyBioRequest createRequest(String nickname, String introduction) throws Exception {
        UpdateMyBioRequest request = new UpdateMyBioRequest();
        Field nicknameField = UpdateMyBioRequest.class.getDeclaredField("nickname");
        nicknameField.setAccessible(true);
        nicknameField.set(request, nickname);
        Field introductionField = UpdateMyBioRequest.class.getDeclaredField("introduction");
        introductionField.setAccessible(true);
        introductionField.set(request, introduction);
        return request;
    }

    @Test
    @DisplayName("소개글 50자 입력 시 유효성 검사 통과")
    void shouldPassValidation_whenIntroduction50Chars() throws Exception {
        // given
        String intro50 = "가".repeat(50);
        UpdateMyBioRequest request = createRequest("닉네임", intro50);

        // when
        Set<ConstraintViolation<UpdateMyBioRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("소개글 51자 입력 시 유효성 검사 실패")
    void shouldFailValidation_whenIntroductionExceeds50Chars() throws Exception {
        // given
        String intro51 = "가".repeat(51);
        UpdateMyBioRequest request = createRequest("닉네임", intro51);

        // when
        Set<ConstraintViolation<UpdateMyBioRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("50자");
    }

    @Test
    @DisplayName("닉네임 21자 입력 시 유효성 검사 실패")
    void shouldFailValidation_whenNicknameExceeds20Chars() throws Exception {
        // given
        String nickname21 = "가".repeat(21);
        UpdateMyBioRequest request = createRequest(nickname21, "소개글");

        // when
        Set<ConstraintViolation<UpdateMyBioRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("20자");
    }
}
