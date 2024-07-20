package com.pfplaybackend.api.partyroom.exception;

// "status": null,
// "code": 500, 상태 코드
// "message": "Empty playlist is not allowed",
// "errorCode": null
public class InvalidDjException extends RuntimeException {
    public InvalidDjException(String message) {
        super(message);
    }
}
