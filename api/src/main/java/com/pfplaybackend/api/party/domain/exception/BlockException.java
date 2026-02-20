package com.pfplaybackend.api.party.domain.exception;

import com.pfplaybackend.api.common.exception.DomainException;
import com.pfplaybackend.api.common.exception.http.BadRequestException;
import com.pfplaybackend.api.common.exception.http.NotFoundException;
import lombok.Getter;

@Getter
public enum BlockException implements DomainException {

    BLOCK_HISTORY_NOT_FOUND("BLK-001", "No block history found", NotFoundException.class),
    ALREADY_BLOCKED_CREW("BLK-002", "Crew member is already blocked", BadRequestException.class);

    private final String errorCode;
    private final String message;
    private final Class<?> aClass;

    BlockException(String errorCode, String message, Class<?> aClass) {
        this.message = message;
        this.errorCode = errorCode;
        this.aClass = aClass;
    }
}
