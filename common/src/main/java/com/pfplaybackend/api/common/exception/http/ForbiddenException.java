package com.pfplaybackend.api.common.exception.http;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends AbstractHTTPException {
    public ForbiddenException(String errorCode, String message) {
        super(HttpStatus.FORBIDDEN, errorCode, message);
    }
}
