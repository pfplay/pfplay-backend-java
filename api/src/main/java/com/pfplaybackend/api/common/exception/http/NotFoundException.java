package com.pfplaybackend.api.common.exception.http;

import org.springframework.http.HttpStatus;

public class NotFoundException extends AbstractHTTPException {
    public NotFoundException(String errorCode, String message) {
        super(HttpStatus.FORBIDDEN, errorCode, message);
    }
}