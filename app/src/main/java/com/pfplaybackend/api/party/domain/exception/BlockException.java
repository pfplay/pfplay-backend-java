package com.pfplaybackend.api.party.domain.exception;

import com.pfplaybackend.api.common.exception.DomainException;
import com.pfplaybackend.api.common.exception.ErrorType;
import lombok.Getter;

@Getter
public enum BlockException implements DomainException {

    BLOCK_HISTORY_NOT_FOUND("BLK-001", "차단 이력을 찾을 수 없습니다", ErrorType.NOT_FOUND),
    ALREADY_BLOCKED_CREW("BLK-002", "이미 차단된 크루입니다", ErrorType.CONFLICT);

    private final String errorCode;
    private final String message;
    private final ErrorType errorType;

    BlockException(String errorCode, String message, ErrorType errorType) {
        this.message = message;
        this.errorCode = errorCode;
        this.errorType = errorType;
    }
}
