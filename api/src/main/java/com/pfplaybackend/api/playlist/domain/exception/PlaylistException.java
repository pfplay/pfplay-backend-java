package com.pfplaybackend.api.playlist.domain.exception;

import com.pfplaybackend.api.common.exception.DomainException;
import com.pfplaybackend.api.common.exception.http.ConflictException;
import com.pfplaybackend.api.common.exception.http.ForbiddenException;
import com.pfplaybackend.api.common.exception.http.NotFoundException;
import lombok.Getter;

@Getter
public enum PlaylistException implements DomainException {

    NO_WALLET("PLL-001", "Wallet connection required", ForbiddenException.class),
    EXCEEDED_PLAYLIST_LIMIT("PLL-002", "Playlist limit exceeded", ConflictException.class),
    NOT_FOUND_PLAYLIST("PLL-003", "Playlist not found", NotFoundException.class);

    private final String errorCode;
    private final String message;
    private final Class<?> aClass;

    PlaylistException(String errorCode, String message, Class<?> aClass) {
        this.message = message;
        this.errorCode = errorCode;
        this.aClass = aClass;
    }
}
