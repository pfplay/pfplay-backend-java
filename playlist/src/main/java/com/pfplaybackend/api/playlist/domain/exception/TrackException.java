package com.pfplaybackend.api.playlist.domain.exception;

import com.pfplaybackend.api.common.exception.DomainException;
import com.pfplaybackend.api.common.exception.ErrorType;
import lombok.Getter;

@Getter
public enum TrackException implements DomainException {

    DUPLICATE_TRACK_IN_PLAYLIST("TRK-001", "이미 플레이리스트에 존재하는 트랙입니다", ErrorType.CONFLICT),
    EXCEEDED_TRACK_LIMIT("TRK-002", "플레이리스트의 트랙 한도를 초과했습니다", ErrorType.CONFLICT),
    NOT_FOUND_TRACK("TRK-003", "트랙을 찾을 수 없습니다", ErrorType.NOT_FOUND),
    INVALID_TRACK_ORDER("TRK-004", "유효하지 않은 트랙 순서입니다", ErrorType.BAD_REQUEST);

    private final String errorCode;
    private final String message;
    private final ErrorType errorType;

    TrackException(String errorCode, String message, ErrorType errorType) {
        this.message = message;
        this.errorCode = errorCode;
        this.errorType = errorType;
    }
}
