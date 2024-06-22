package com.pfplaybackend.api.partyroom.exception;

import lombok.Getter;

@Getter
public class InvalidPartyroomIdRequestException extends RuntimeException {
    public InvalidPartyroomIdRequestException(String message) {
        super(message);
    }
}
