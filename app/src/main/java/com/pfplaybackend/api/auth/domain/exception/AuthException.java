package com.pfplaybackend.api.auth.domain.exception;

import com.pfplaybackend.api.common.exception.DomainException;
import com.pfplaybackend.api.common.exception.ErrorType;
import lombok.Getter;

@Getter
public enum AuthException implements DomainException {
    CODE_CHALLENGE_FAILED("AUTH-001", "코드 챌린지 생성에 실패했습니다", ErrorType.BAD_REQUEST),
    PROVIDER_NOT_CONFIGURED("AUTH-002", "OAuth 제공자가 설정되지 않았습니다", ErrorType.BAD_REQUEST),
    INVALID_PROVIDER("AUTH-003", "유효하지 않은 OAuth 제공자입니다", ErrorType.BAD_REQUEST),
    INVALID_STATE("AUTH-004", "유효하지 않거나 만료된 state 파라미터입니다", ErrorType.BAD_REQUEST);

    private final String errorCode;
    private final String message;
    private final ErrorType errorType;

    AuthException(String errorCode, String message, ErrorType errorType) {
        this.errorCode = errorCode;
        this.message = message;
        this.errorType = errorType;
    }
}
