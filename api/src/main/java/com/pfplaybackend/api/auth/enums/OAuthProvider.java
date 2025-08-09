package com.pfplaybackend.api.auth.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OAuthProvider {
    GOOGLE("google"),
    TWITTER("twitter");

    private final String value;

    public static OAuthProvider fromString(String value) {
        for (OAuthProvider provider : OAuthProvider.values()) {
            if (provider.value.equalsIgnoreCase(value)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("Unknown OAuth provider: " + value);
    }
}