package com.pfplaybackend.api.auth.application.dto.command;

public record OAuthLoginCommand(String provider, String code, String codeVerifier) {
}
