package com.pfplaybackend.api.party.domain.exception;

import com.pfplaybackend.api.common.exception.DomainException;
import com.pfplaybackend.api.common.exception.ErrorType;
import lombok.Getter;

@Getter
public enum PenaltyException implements DomainException {
    PERMANENT_EXPULSION("PNT-001", "Banned User", ErrorType.FORBIDDEN),
    PENALTY_HISTORY_NOT_FOUND("PNT-002", "No penalty history found", ErrorType.NOT_FOUND);

    private final String errorCode;
    private final String message;
    private final ErrorType errorType;

    PenaltyException(String errorCode, String message, ErrorType errorType) {
        this.message = message;
        this.errorCode = errorCode;
        this.errorType = errorType;
    }
}
