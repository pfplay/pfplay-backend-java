package com.pfplaybackend.api.playlist.exception;

public class PlayListLimitExceededException extends RuntimeException {

    public PlayListLimitExceededException(String message) {
        super(message);
    }
}

