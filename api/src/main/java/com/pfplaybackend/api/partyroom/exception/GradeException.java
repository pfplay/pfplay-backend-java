package com.pfplaybackend.api.partyroom.exception;

import com.pfplaybackend.api.common.exception.DomainException;
import com.pfplaybackend.api.common.exception.http.ForbiddenException;
import lombok.Getter;

@Getter
public enum GradeException implements DomainException {
    UNABLE_TO_SET_HOST("GRD-001", "Unable to set Host", ForbiddenException .class),
    ADJUSTER_GRADE_NOT_MANAGER("GRD-002", "Adjuster grade is not Manager", ForbiddenException .class),
    ADJUSTER_GRADE_LOWER_THAN_SUBJECT("GRD-003", "Adjuster grade is lower than Subject", ForbiddenException .class),
    TARGET_GRADE_HIGHER_THAN_ADJUSTER("GRD-004", "Target grade is higher than Adjuster", ForbiddenException .class);

    private final String errorCode;
    private final String message;
    private final Class<?> aClass;

    GradeException(String errorCode, String message, Class<?> aClass) {
        this.message = message;
        this.errorCode = errorCode;
        this.aClass = aClass;
    }
}
