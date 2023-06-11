package com.pfplaybackend.api.common;

import lombok.Data;

@Data
public class ResponseMessage<T> {

    private int status;
    private String message;

    public ResponseMessage(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public static <T> ResponseMessage<T> make(int status, String message) {
        return new ResponseMessage<>(status, message);
    }
}
