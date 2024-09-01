package com.pfplaybackend.api.partyroom.exception;

import com.pfplaybackend.api.common.exception.DomainException;
import com.pfplaybackend.api.common.exception.http.ForbiddenException;
import lombok.Getter;

@Getter
public enum GradeException implements DomainException {
    MANAGER_GRADE_REQUIRED("GRD-001", "Manager grade is required to perform this action", ForbiddenException .class),
    UNABLE_TO_SET_HOST("GRD-002", "Unable to set Host", ForbiddenException .class),
    GRADE_INSUFFICIENT_FOR_OPERATION("GRD-003", "The current grade level is insufficient to perform the requested operation", ForbiddenException .class),
    GRADE_EXCEEDS_ALLOWED_THRESHOLD("GRD-004", "The specified grade level exceeds the allowed threshold", ForbiddenException .class);

    private final String errorCode;
    private final String message;
    private final Class<?> aClass;

    GradeException(String errorCode, String message, Class<?> aClass) {
        this.message = message;
        this.errorCode = errorCode;
        this.aClass = aClass;
    }
}
