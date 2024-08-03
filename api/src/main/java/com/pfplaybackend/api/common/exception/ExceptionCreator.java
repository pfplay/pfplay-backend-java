package com.pfplaybackend.api.common.exception;

import com.pfplaybackend.api.common.exception.http.AbstractHTTPException;

public class ExceptionCreator {

    public static AbstractHTTPException create(DomainException domainException) {
        String errorCode = domainException.getErrorCode();
        String message = domainException.getMessage();
        return (AbstractHTTPException) InstanceCreator.createInstance(domainException.getAClass(), errorCode, message);
    }
}