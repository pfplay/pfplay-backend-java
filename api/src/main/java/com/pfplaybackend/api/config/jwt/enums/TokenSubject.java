package com.pfplaybackend.api.config.jwt.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenSubject {
    ACCESS_TOKEN_SUBJECT("AccessToken");

    final private String value;
}
