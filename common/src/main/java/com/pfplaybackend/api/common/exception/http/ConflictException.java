package com.pfplaybackend.api.common.exception.http;

import org.springframework.http.HttpStatus;

public class ConflictException extends AbstractHTTPException {
    public ConflictException(String errorCode, String message) {
        super(HttpStatus.CONFLICT, errorCode, message);
    }
}