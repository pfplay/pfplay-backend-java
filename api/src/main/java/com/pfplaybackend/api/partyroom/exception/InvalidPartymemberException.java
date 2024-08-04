package com.pfplaybackend.api.partyroom.exception;

public class InvalidPartymemberException extends RuntimeException{
    private static String message = "Invalid Partymember requested to subscribe in socket session";

    public InvalidPartymemberException() {
        super(message);
    }
}
