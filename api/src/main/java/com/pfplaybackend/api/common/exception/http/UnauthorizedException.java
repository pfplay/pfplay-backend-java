package com.pfplaybackend.api.common.exception.http;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends AbstractHTTPException {
    public UnauthorizedException(String errorCode, String message) {
        super(HttpStatus.UNAUTHORIZED, errorCode, message);
    }
}