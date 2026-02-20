package com.pfplaybackend.api.common;

import org.springframework.http.HttpStatus;

public record ApiErrorResponse(int status, String errorCode, String message) {
    public static ApiErrorResponse of(HttpStatus status, String errorCode, String message) {
        return new ApiErrorResponse(status.value(), errorCode, message);
    }
}
