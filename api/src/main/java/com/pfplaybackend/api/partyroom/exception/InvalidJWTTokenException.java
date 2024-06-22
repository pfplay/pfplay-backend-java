package com.pfplaybackend.api.partyroom.exception;

import lombok.Getter;

@Getter
public class InvalidJWTTokenException extends RuntimeException {
    public InvalidJWTTokenException(String message) {
        super(message);
    }
}
