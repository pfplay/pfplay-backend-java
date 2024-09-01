package com.pfplaybackend.api.partyroom.exception;

import com.pfplaybackend.api.common.exception.DomainException;
import com.pfplaybackend.api.common.exception.http.ForbiddenException;
import com.pfplaybackend.api.common.exception.http.NotFoundException;
import lombok.Getter;

@Getter
public enum PartyroomException implements DomainException {
    NOT_FOUND_ROOM("PTR-001", "Can not find Partyroom", NotFoundException.class),
    ALREADY_TERMINATED("PTR-002", "Already Terminated Partyroom", ForbiddenException.class),
    EXCEEDED_LIMIT("PTR-003", "Exceeded Entrance Limit", ForbiddenException.class),
    ACTIVE_ANOTHER_ROOM("PTR-004", "Already Active in Another Partyroom", ForbiddenException.class),
    CACHE_MISSED_SESSION("PTR-005", "No cached data found for sessionId", NotFoundException.class);

    private final String errorCode;
    private final String message;
    private final Class<?> aClass;

    PartyroomException(String errorCode, String message, Class<?> aClass) {
        this.message = message;
        this.errorCode = errorCode;
        this.aClass = aClass;
    }
}