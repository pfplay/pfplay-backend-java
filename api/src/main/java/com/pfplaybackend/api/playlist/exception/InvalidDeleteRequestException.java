package com.pfplaybackend.api.playlist.exception;

public class InvalidDeleteRequestException extends RuntimeException {
    public InvalidDeleteRequestException(String message) {
        super(message);
    }
}

