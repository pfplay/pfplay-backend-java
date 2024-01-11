package com.pfplaybackend.api.playlist.exception;

public class PlayListMusicLimitExceededException extends RuntimeException {

    public PlayListMusicLimitExceededException(String message) {
        super(message);
    }
}

