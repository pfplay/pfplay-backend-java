package com.pfplaybackend.api.playlist.adapter.in.web.payload.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class MusicSearchRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("platform이 null이어도 유효성 검사를 통과해야 한다")
    void shouldPassValidationWhenPlatformIsNull() {
        // given
        MusicSearchRequest request = new MusicSearchRequest("test query", null);

        // when
        Set<ConstraintViolation<MusicSearchRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("q가 null이면 유효성 검사에 실패해야 한다")
    void shouldFailValidationWhenQIsNull() {
        // given
        MusicSearchRequest request = new MusicSearchRequest(null, null);

        // when
        Set<ConstraintViolation<MusicSearchRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("q cannot be null");
    }
}
