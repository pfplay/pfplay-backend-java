package com.pfplaybackend.api.auth.domain.exception;

import com.pfplaybackend.api.common.exception.DomainException;
import com.pfplaybackend.api.common.exception.ErrorType;
import lombok.Getter;

@Getter
public enum AuthException implements DomainException {
    CODE_CHALLENGE_FAILED("AUTH-001", "Failed to generate code challenge", ErrorType.BAD_REQUEST),
    PROVIDER_NOT_CONFIGURED("AUTH-002", "OAuth provider not configured", ErrorType.BAD_REQUEST);

    private final String errorCode;
    private final String message;
    private final ErrorType errorType;

    AuthException(String errorCode, String message, ErrorType errorType) {
        this.errorCode = errorCode;
        this.message = message;
        this.errorType = errorType;
    }
}
