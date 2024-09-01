package com.pfplaybackend.api.playlist.exception;

import com.pfplaybackend.api.common.exception.DomainException;
import com.pfplaybackend.api.common.exception.http.ConflictException;
import com.pfplaybackend.api.common.exception.http.ForbiddenException;
import lombok.Getter;

@Getter
public enum PlaylistMusicException  implements DomainException {

    DUPLICATE_MUSIC_IN_PLAYLIST("PLM-001", "Music cannot be added to the playlist because it already exists", ConflictException.class);

    private final String errorCode;
    private final String message;
    private final Class<?> aClass;

    PlaylistMusicException(String errorCode, String message, Class<?> aClass) {
        this.message = message;
        this.errorCode = errorCode;
        this.aClass = aClass;
    }
}
