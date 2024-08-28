package com.pfplaybackend.api.partyroom.exception;

import com.pfplaybackend.api.common.exception.DomainException;
import com.pfplaybackend.api.common.exception.http.ForbiddenException;
import com.pfplaybackend.api.common.exception.http.NotFoundException;
import lombok.Getter;

@Getter
public enum PartymemberException implements DomainException {
    NOT_FOUND_ACTIVE_ROOM("PTM-001", "Can not find My Active Room", NotFoundException.class);

    private final String errorCode;
    private final String message;
    private final Class<?> aClass;

    PartymemberException(String errorCode, String message, Class<?> aClass) {
        this.message = message;
        this.errorCode = errorCode;
        this.aClass = aClass;
    }
}
