package com.pfplaybackend.api.partyplay.exception;

import com.pfplaybackend.api.common.exception.DomainException;
import com.pfplaybackend.api.common.exception.http.ForbiddenException;
import lombok.Getter;

@Getter
public enum PenaltyException implements DomainException {
    PERMANENT_EXPULSION("PNT-001", "Banned User",ForbiddenException.class);

    private final String errorCode;
    private final String message;
    private final Class<?> aClass;

    PenaltyException(String errorCode, String message, Class<?> aClass) {
        this.message = message;
        this.errorCode = errorCode;
        this.aClass = aClass;
    }
}

