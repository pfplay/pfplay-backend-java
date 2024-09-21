package com.pfplaybackend.api.config.jwt.exception;

import com.pfplaybackend.api.common.exception.SecurityException;
import com.pfplaybackend.api.common.exception.http.UnauthorizedException;
import lombok.Getter;

@Getter
public enum JwtAuthenticationException implements SecurityException {
    ACCESS_TOKEN_NOT_FOUND("JWT-001", "Json Web Token not found", UnauthorizedException.class),
    ACCESS_TOKEN_INVALID("JWT-002", "Json Web Token is not valid", UnauthorizedException.class),
    ACCESS_TOKEN_EXPIRED("JWT-003", "Json Web Token is expired", UnauthorizedException.class);

    private final String errorCode;
    private final String message;
    private final Class<?> aClass;

    JwtAuthenticationException(String errorCode, String message, Class<?> aClass) {
        this.errorCode = errorCode;
        this.message = message;
        this.aClass = aClass;
    }
}
