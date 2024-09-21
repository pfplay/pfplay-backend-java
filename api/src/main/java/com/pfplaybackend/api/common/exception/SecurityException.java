package com.pfplaybackend.api.common.exception;

public interface SecurityException {
    String getErrorCode();
    String getMessage();
    Class<?> getAClass();
}
