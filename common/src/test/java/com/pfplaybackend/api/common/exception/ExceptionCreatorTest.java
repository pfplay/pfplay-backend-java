package com.pfplaybackend.api.common.exception;

import com.pfplaybackend.api.common.exception.http.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExceptionCreatorTest {

    @Test
    @DisplayName("create — BAD_REQUEST ErrorType이면 BadRequestException을 생성한다")
    void create_badRequest() {
        // given
        DomainException domainException = mock(DomainException.class);
        when(domainException.getErrorType()).thenReturn(ErrorType.BAD_REQUEST);
        when(domainException.getErrorCode()).thenReturn("TEST-001");
        when(domainException.getMessage()).thenReturn("Bad request error");

        // when
        AbstractHTTPException result = ExceptionCreator.create(domainException);

        // then
        assertThat(result).isInstanceOf(BadRequestException.class);
        assertThat(result.getErrorCode()).isEqualTo("TEST-001");
        assertThat(result.getMessage()).isEqualTo("Bad request error");
    }

    @Test
    @DisplayName("create — NOT_FOUND ErrorType이면 NotFoundException을 생성한다")
    void create_notFound() {
        // given
        DomainException domainException = mock(DomainException.class);
        when(domainException.getErrorType()).thenReturn(ErrorType.NOT_FOUND);
        when(domainException.getErrorCode()).thenReturn("TEST-002");
        when(domainException.getMessage()).thenReturn("Not found error");

        // when
        AbstractHTTPException result = ExceptionCreator.create(domainException);

        // then
        assertThat(result).isInstanceOf(NotFoundException.class);
        assertThat(result.getErrorCode()).isEqualTo("TEST-002");
        assertThat(result.getMessage()).isEqualTo("Not found error");
    }

    @Test
    @DisplayName("create — FORBIDDEN ErrorType이면 ForbiddenException을 생성한다")
    void create_forbidden() {
        // given
        DomainException domainException = mock(DomainException.class);
        when(domainException.getErrorType()).thenReturn(ErrorType.FORBIDDEN);
        when(domainException.getErrorCode()).thenReturn("TEST-003");
        when(domainException.getMessage()).thenReturn("Forbidden error");

        // when
        AbstractHTTPException result = ExceptionCreator.create(domainException);

        // then
        assertThat(result).isInstanceOf(ForbiddenException.class);
        assertThat(result.getErrorCode()).isEqualTo("TEST-003");
        assertThat(result.getMessage()).isEqualTo("Forbidden error");
    }

    @Test
    @DisplayName("create — UNAUTHORIZED ErrorType이면 UnauthorizedException을 생성한다")
    void create_unauthorized() {
        // given
        DomainException domainException = mock(DomainException.class);
        when(domainException.getErrorType()).thenReturn(ErrorType.UNAUTHORIZED);
        when(domainException.getErrorCode()).thenReturn("TEST-004");
        when(domainException.getMessage()).thenReturn("Unauthorized error");

        // when
        AbstractHTTPException result = ExceptionCreator.create(domainException);

        // then
        assertThat(result).isInstanceOf(UnauthorizedException.class);
        assertThat(result.getErrorCode()).isEqualTo("TEST-004");
        assertThat(result.getMessage()).isEqualTo("Unauthorized error");
    }

    @Test
    @DisplayName("create — CONFLICT ErrorType이면 ConflictException을 생성한다")
    void create_conflict() {
        // given
        DomainException domainException = mock(DomainException.class);
        when(domainException.getErrorType()).thenReturn(ErrorType.CONFLICT);
        when(domainException.getErrorCode()).thenReturn("TEST-005");
        when(domainException.getMessage()).thenReturn("Conflict error");

        // when
        AbstractHTTPException result = ExceptionCreator.create(domainException);

        // then
        assertThat(result).isInstanceOf(ConflictException.class);
        assertThat(result.getErrorCode()).isEqualTo("TEST-005");
        assertThat(result.getMessage()).isEqualTo("Conflict error");
    }
}
