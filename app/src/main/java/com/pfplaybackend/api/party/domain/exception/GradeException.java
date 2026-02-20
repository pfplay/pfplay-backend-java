package com.pfplaybackend.api.party.domain.exception;

import com.pfplaybackend.api.common.exception.DomainException;
import com.pfplaybackend.api.common.exception.ErrorType;
import lombok.Getter;

@Getter
public enum GradeException implements DomainException {
    MANAGER_GRADE_REQUIRED("GRD-001", "Manager grade is required to perform this action", ErrorType.FORBIDDEN),
    UNABLE_TO_SET_HOST("GRD-002", "Unable to set Host", ErrorType.FORBIDDEN),
    GRADE_INSUFFICIENT_FOR_OPERATION("GRD-003", "The current grade level is insufficient to perform the requested operation", ErrorType.FORBIDDEN),
    GRADE_EXCEEDS_ALLOWED_THRESHOLD("GRD-004", "The specified grade level exceeds the allowed threshold", ErrorType.FORBIDDEN),
    GUEST_ONLY_POSSIBLE_LISTENER("GRD-005", "Guest is Only possible Listener", ErrorType.FORBIDDEN);

    private final String errorCode;
    private final String message;
    private final ErrorType errorType;

    GradeException(String errorCode, String message, ErrorType errorType) {
        this.message = message;
        this.errorCode = errorCode;
        this.errorType = errorType;
    }
}
