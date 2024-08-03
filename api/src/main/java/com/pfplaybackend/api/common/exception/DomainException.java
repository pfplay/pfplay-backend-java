package com.pfplaybackend.api.common.exception;

public interface DomainException {
    String getErrorCode();
    String getMessage();
    Class<?> getAClass();
}