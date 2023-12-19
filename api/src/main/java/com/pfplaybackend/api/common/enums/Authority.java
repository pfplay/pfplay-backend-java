package com.pfplaybackend.api.common.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Authority {

    @Schema(description = "유저")
    ROLE_USER("USER"),

    @Schema(description = "게스트")
    ROLE_GUEST("GUEST"),

    @Schema(description = "지갑연동")
    ROLE_WALLET_USER("WALLET_USER");

    private final String role;
}