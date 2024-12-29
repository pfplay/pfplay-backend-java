package com.pfplaybackend.api.partyplay.exception;

import com.pfplaybackend.api.common.exception.DomainException;
import com.pfplaybackend.api.common.exception.http.ConflictException;
import com.pfplaybackend.api.common.exception.http.ForbiddenException;
import lombok.Getter;

@Getter
public enum DjException implements DomainException {
    ALREADY_REGISTERED("DJ-001", "Already Registered Dj", ConflictException.class),
    QUEUE_CLOSED("DJ-002", "Dj Queue is Closed", ForbiddenException.class),
    EMPTY_PLAYLIST("DJ-003", "Cannot Register Empty Playlist", ForbiddenException.class);

    private final String errorCode;
    private final String message;
    private final Class<?> aClass;

    DjException(String errorCode, String message, Class<?> aClass) {
        this.message = message;
        this.errorCode = errorCode;
        this.aClass = aClass;
    }
}
