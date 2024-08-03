package com.pfplaybackend.api.partyroom.exception;

import com.pfplaybackend.api.common.exception.DomainException;
import com.pfplaybackend.api.common.exception.http.ForbiddenException;
import lombok.Getter;

@Getter
public enum DjException implements DomainException {
    ALREADY_REGISTERED("DJ-001", "Already Registered Dj", ForbiddenException.class);

    private final String errorCode;
    private final String message;
    private final Class<?> aClass;

    DjException(String errorCode, String message, Class<?> aClass) {
        this.message = message;
        this.errorCode = errorCode;
        this.aClass = aClass;
    }
}
