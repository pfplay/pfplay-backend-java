package com.pfplaybackend.api.playlist.domain.exception;

import com.pfplaybackend.api.common.exception.DomainException;
import com.pfplaybackend.api.common.exception.ErrorType;
import lombok.Getter;

@Getter
public enum PlaylistException implements DomainException {

    NO_WALLET("PLL-001", "Wallet connection required", ErrorType.FORBIDDEN),
    EXCEEDED_PLAYLIST_LIMIT("PLL-002", "Playlist limit exceeded", ErrorType.CONFLICT),
    NOT_FOUND_PLAYLIST("PLL-003", "Playlist not found", ErrorType.NOT_FOUND);

    private final String errorCode;
    private final String message;
    private final ErrorType errorType;

    PlaylistException(String errorCode, String message, ErrorType errorType) {
        this.message = message;
        this.errorCode = errorCode;
        this.errorType = errorType;
    }
}
