package com.pfplaybackend.api.user.presentation.request;

import lombok.Getter;

@Getter
public class TokenRequest {
    private String accessToken;

    public TokenRequest() { }

    public TokenRequest(String accessToken) {
        this.accessToken = accessToken;
    }
}
