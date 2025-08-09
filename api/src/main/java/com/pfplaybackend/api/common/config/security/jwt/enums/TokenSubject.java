package com.pfplaybackend.api.common.config.security.jwt.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenSubject {
    ACCESS_TOKEN_SUBJECT("AccessToken");

    final private String value;
}
