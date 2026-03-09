package com.pfplaybackend.api.playlist.domain.exception;

import com.pfplaybackend.api.common.exception.DomainException;
import com.pfplaybackend.api.common.exception.ErrorType;
import lombok.Getter;

@Getter
public enum PlaylistException implements DomainException {

    NO_WALLET("PLL-001", "지갑 연결이 필요합니다", ErrorType.FORBIDDEN),
    EXCEEDED_PLAYLIST_LIMIT("PLL-002", "플레이리스트 생성 한도를 초과했습니다", ErrorType.CONFLICT),
    NOT_FOUND_PLAYLIST("PLL-003", "플레이리스트를 찾을 수 없습니다", ErrorType.NOT_FOUND);

    private final String errorCode;
    private final String message;
    private final ErrorType errorType;

    PlaylistException(String errorCode, String message, ErrorType errorType) {
        this.message = message;
        this.errorCode = errorCode;
        this.errorType = errorType;
    }
}
