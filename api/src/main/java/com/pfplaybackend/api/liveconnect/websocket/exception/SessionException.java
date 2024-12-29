package com.pfplaybackend.api.liveconnect.websocket.exception;

import com.pfplaybackend.api.common.exception.DomainException;
import com.pfplaybackend.api.common.exception.http.UnauthorizedException;
import lombok.Getter;

@Getter
public enum SessionException implements DomainException {
    UNAUTHORIZED_SESSION("SESS-001", "Unauthorized Session Requested", UnauthorizedException.class);

    private final String errorCode;
    private final String message;
    private final Class<?> aClass;

    SessionException(String errorCode, String message, Class<?> aClass) {
        this.message = message;
        this.errorCode = errorCode;
        this.aClass = aClass;
    }
}