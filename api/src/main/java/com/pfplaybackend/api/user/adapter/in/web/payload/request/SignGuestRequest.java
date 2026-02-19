package com.pfplaybackend.api.user.adapter.in.web.payload.request;

import lombok.Getter;

@Getter
public class SignGuestRequest {
    private String userAgent;
    public SignGuestRequest() { }
    public SignGuestRequest(String userAgent) {
        this.userAgent = userAgent;
    }
}
