package com.pfplaybackend.api.playlist.domain.exception;

import com.pfplaybackend.api.common.exception.DomainException;
import com.pfplaybackend.api.common.exception.http.BadRequestException;
import com.pfplaybackend.api.common.exception.http.ConflictException;
import com.pfplaybackend.api.common.exception.http.NotFoundException;
import lombok.Getter;

@Getter
public enum TrackException implements DomainException {

    DUPLICATE_TRACK_IN_PLAYLIST("TRK-001", "Track cannot be added to the playlist because it already exists", ConflictException.class),
    EXCEEDED_TRACK_LIMIT("TRK-002", "Track limit exceeded for this playlist", ConflictException.class),
    NOT_FOUND_TRACK("TRK-003", "Track does not exist", NotFoundException.class),
    INVALID_TRACK_ORDER("TRK-004", "Invalid track order number", BadRequestException.class);

    private final String errorCode;
    private final String message;
    private final Class<?> aClass;

    TrackException(String errorCode, String message, Class<?> aClass) {
        this.message = message;
        this.errorCode = errorCode;
        this.aClass = aClass;
    }
}
