package com.pfplaybackend.api.party.domain.exception;

import com.pfplaybackend.api.common.exception.DomainException;
import com.pfplaybackend.api.common.exception.ErrorType;
import lombok.Getter;

@Getter
public enum DjException implements DomainException {
    ALREADY_REGISTERED("DJ-001", "Already Registered Dj", ErrorType.CONFLICT),
    QUEUE_CLOSED("DJ-002", "Dj Queue is Closed", ErrorType.FORBIDDEN),
    EMPTY_PLAYLIST("DJ-003", "Cannot Register Empty Playlist", ErrorType.FORBIDDEN),
    NOT_FOUND_DJ("DJ-004", "DJ not found in queue", ErrorType.BAD_REQUEST);

    private final String errorCode;
    private final String message;
    private final ErrorType errorType;

    DjException(String errorCode, String message, ErrorType errorType) {
        this.message = message;
        this.errorCode = errorCode;
        this.errorType = errorType;
    }
}
