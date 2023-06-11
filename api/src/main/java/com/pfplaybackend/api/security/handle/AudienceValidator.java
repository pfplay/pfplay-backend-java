package com.pfplaybackend.api.security.handle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Objects;

@Slf4j
public class AudienceValidator implements OAuth2TokenValidator<Jwt> {
    private final Instant now = Instant.now();
    OAuth2Error error = new OAuth2Error("invalid_token", "The required audience is missing", null);

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        if (Objects.requireNonNull(token.getExpiresAt()).isAfter(now)) {
            return OAuth2TokenValidatorResult.success();
        } else {
            return OAuth2TokenValidatorResult.failure(error);
        }
    }
}