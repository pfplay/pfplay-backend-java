package com.pfplaybackend.api.party.domain.exception;

import com.pfplaybackend.api.common.exception.DomainException;
import com.pfplaybackend.api.common.exception.http.ForbiddenException;
import lombok.Getter;

@Getter
public enum ReactionException implements DomainException {
    INVALID_REACTION("RCT-001", "Invalid reaction type", ForbiddenException.class);

    private final String errorCode;
    private final String message;
    private final Class<?> aClass;

    ReactionException(String errorCode, String message, Class<?> aClass) {
        this.message = message;
        this.errorCode = errorCode;
        this.aClass = aClass;
    }
}
