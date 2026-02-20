package com.pfplaybackend.api.party.domain.exception;

import com.pfplaybackend.api.common.exception.DomainException;
import com.pfplaybackend.api.common.exception.ErrorType;
import lombok.Getter;

@Getter
public enum CrewException implements DomainException {
    NOT_FOUND_ACTIVE_ROOM("CRW-001", "Can not find My Active Room", ErrorType.NOT_FOUND),
    INVALID_ACTIVE_ROOM("CRW-002", "Invalid My Active Room", ErrorType.CONFLICT);

    private final String errorCode;
    private final String message;
    private final ErrorType errorType;

    CrewException(String errorCode, String message, ErrorType errorType) {
        this.message = message;
        this.errorCode = errorCode;
        this.errorType = errorType;
    }
}
