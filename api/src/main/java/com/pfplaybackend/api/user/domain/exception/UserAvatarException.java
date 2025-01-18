package com.pfplaybackend.api.user.domain.exception;

import com.pfplaybackend.api.common.exception.DomainException;
import com.pfplaybackend.api.common.exception.http.ForbiddenException;
import lombok.Getter;

@Getter
public enum UserAvatarException implements DomainException {

    AVATAR_SELECTION_FORBIDDEN("CRW-001", "Cannot Select Due To Restrictions", ForbiddenException.class);

    private final String errorCode;
    private final String message;
    private final Class<?> aClass;

    UserAvatarException(String errorCode, String message, Class<?> aClass) {
        this.message = message;
        this.errorCode = errorCode;
        this.aClass = aClass;
    }
}
