package com.pfplaybackend.api.playlist.exception;

public class PlaylistLimitExceededException extends RuntimeException {

    public PlaylistLimitExceededException(String message) {
        super(message);
    }
}

