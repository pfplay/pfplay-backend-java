package com.pfplaybackend.api.playlist.exception;

public class PlaylistMusicLimitExceededException extends RuntimeException {

    public PlaylistMusicLimitExceededException(String message) {
        super(message);
    }
}

