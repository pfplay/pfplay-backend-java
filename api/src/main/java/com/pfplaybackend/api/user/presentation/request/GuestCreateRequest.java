package com.pfplaybackend.api.user.presentation.request;

import lombok.Getter;

@Getter
public class GuestCreateRequest {
    private String userAgent;

    public GuestCreateRequest() { }

    public GuestCreateRequest(String userAgent) {
        this.userAgent = userAgent;
    }
}