package com.pfplaybackend.api.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthorityRole {
    USER("ROLE_USER", "USER"),
    WALLET_USER("ROLE_WALLET_USER", "WALLET_USER");

    private final String role;
    private final String roleName;
}
