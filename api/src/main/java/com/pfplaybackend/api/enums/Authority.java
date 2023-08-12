package com.pfplaybackend.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Authority {

    ROLE_USER("ROLE_USER"),
    ROLE_GUEST("ROLE_GUEST"),
    ROLE_WALLET_USER("ROLE_WALLET_USER");

    private final String role;
}