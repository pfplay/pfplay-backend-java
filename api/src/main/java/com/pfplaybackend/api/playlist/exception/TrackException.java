package com.pfplaybackend.api.playlist.exception;

import com.pfplaybackend.api.common.exception.DomainException;
import com.pfplaybackend.api.common.exception.http.ConflictException;
import lombok.Getter;

@Getter
public enum TrackException implements DomainException {

    DUPLICATE_TRACK_IN_PLAYLIST("TRK-001", "Track cannot be added to the playlist because it already exists", ConflictException.class),
    EXCEEDED_TRACK_LIMIT("TRK-002", "Track cannot be added to the playlist because it already exists", ConflictException.class);

    private final String errorCode;
    private final String message;
    private final Class<?> aClass;

    TrackException(String errorCode, String message, Class<?> aClass) {
        this.message = message;
        this.errorCode = errorCode;
        this.aClass = aClass;
    }
}
