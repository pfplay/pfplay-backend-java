package com.pfplaybackend.api.common.config.security.jwt.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenClaim {
    UID("uid"),
    EMAIL("email"),
    ACCESS_LEVEL("access_level"),
    AUTHORITY_TIER("authority_tier");

    final private String value;
}
