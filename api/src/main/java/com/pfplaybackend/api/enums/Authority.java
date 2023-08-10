package com.pfplaybackend.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Authority {

    USER("ROLE_USER"),
    GUEST("ROLE_GUEST"),
    WALLET_USER("ROLE_WALLET_USER");

    private final String role;
}