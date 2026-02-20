package com.pfplaybackend.api.party.domain.exception;

import com.pfplaybackend.api.common.exception.DomainException;
import com.pfplaybackend.api.common.exception.ErrorType;
import lombok.Getter;

@Getter
public enum ReactionException implements DomainException {
    INVALID_REACTION("RCT-001", "Invalid reaction type", ErrorType.FORBIDDEN);

    private final String errorCode;
    private final String message;
    private final ErrorType errorType;

    ReactionException(String errorCode, String message, ErrorType errorType) {
        this.message = message;
        this.errorCode = errorCode;
        this.errorType = errorType;
    }
}
