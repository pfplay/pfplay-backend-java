package com.pfplaybackend.api.common.exception.http;

import org.springframework.http.HttpStatus;

public class BadRequestException extends AbstractHTTPException {
    public BadRequestException(String errorCode, String message) {
        super(HttpStatus.FORBIDDEN, errorCode, message);
    }
}