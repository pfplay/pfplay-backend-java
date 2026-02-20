package com.pfplaybackend.api.playlist.domain.exception;

import com.pfplaybackend.api.common.exception.DomainException;
import com.pfplaybackend.api.common.exception.ErrorType;
import lombok.Getter;

@Getter
public enum TrackException implements DomainException {

    DUPLICATE_TRACK_IN_PLAYLIST("TRK-001", "Track cannot be added to the playlist because it already exists", ErrorType.CONFLICT),
    EXCEEDED_TRACK_LIMIT("TRK-002", "Track limit exceeded for this playlist", ErrorType.CONFLICT),
    NOT_FOUND_TRACK("TRK-003", "Track does not exist", ErrorType.NOT_FOUND),
    INVALID_TRACK_ORDER("TRK-004", "Invalid track order number", ErrorType.BAD_REQUEST);

    private final String errorCode;
    private final String message;
    private final ErrorType errorType;

    TrackException(String errorCode, String message, ErrorType errorType) {
        this.message = message;
        this.errorCode = errorCode;
        this.errorType = errorType;
    }
}
