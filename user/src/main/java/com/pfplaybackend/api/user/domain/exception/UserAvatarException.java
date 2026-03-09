package com.pfplaybackend.api.user.domain.exception;

import com.pfplaybackend.api.common.exception.DomainException;
import com.pfplaybackend.api.common.exception.ErrorType;
import lombok.Getter;

@Getter
public enum UserAvatarException implements DomainException {

    AVATAR_SELECTION_FORBIDDEN("UAV-001", "제한으로 인해 선택할 수 없습니다", ErrorType.FORBIDDEN);

    private final String errorCode;
    private final String message;
    private final ErrorType errorType;

    UserAvatarException(String errorCode, String message, ErrorType errorType) {
        this.message = message;
        this.errorCode = errorCode;
        this.errorType = errorType;
    }
}
