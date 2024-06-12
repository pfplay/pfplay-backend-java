package com.pfplaybackend.api.user.presentation.payload.request;

import lombok.Getter;

@Getter
public class SignGuestRequest {
    private String userAgent;
    public SignGuestRequest() { }
    public SignGuestRequest(String userAgent) {
        this.userAgent = userAgent;
    }
}
