package com.pfplaybackend.api.partyroom.exception;

import com.pfplaybackend.api.common.exception.DomainException;
import com.pfplaybackend.api.common.exception.http.ConflictException;
import com.pfplaybackend.api.common.exception.http.NotFoundException;
import lombok.Getter;

@Getter
public enum CrewException implements DomainException {
    NOT_FOUND_ACTIVE_ROOM("CRW-001", "Can not find My Active Room", NotFoundException.class),
    INVALID_ACTIVE_ROOM("CRW-002", "Invalid My Active Room", ConflictException.class);

    private final String errorCode;
    private final String message;
    private final Class<?> aClass;

    CrewException(String errorCode, String message, Class<?> aClass) {
        this.message = message;
        this.errorCode = errorCode;
        this.aClass = aClass;
    }
}
