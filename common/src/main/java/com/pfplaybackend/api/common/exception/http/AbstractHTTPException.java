package com.pfplaybackend.api.common.exception.http;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class AbstractHTTPException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;
    private final String message;

    public AbstractHTTPException(HttpStatus status, String errorCode, String message) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
    }
}
