package com.pfplaybackend.api.auth.application.dto.result;

public record OAuthUrlResult(String authUrl, String state, String provider, Long expiresIn) {
}
