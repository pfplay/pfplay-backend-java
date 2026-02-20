package com.pfplaybackend.api.party.domain.exception;

import com.pfplaybackend.api.common.exception.DomainException;
import com.pfplaybackend.api.common.exception.ErrorType;
import lombok.Getter;

@Getter
public enum BlockException implements DomainException {

    BLOCK_HISTORY_NOT_FOUND("BLK-001", "No block history found", ErrorType.NOT_FOUND),
    ALREADY_BLOCKED_CREW("BLK-002", "Crew member is already blocked", ErrorType.BAD_REQUEST);

    private final String errorCode;
    private final String message;
    private final ErrorType errorType;

    BlockException(String errorCode, String message, ErrorType errorType) {
        this.message = message;
        this.errorCode = errorCode;
        this.errorType = errorType;
    }
}
