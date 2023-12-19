package com.pfplaybackend.api.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApiHeader {
    AUTHORIZATION("Authorization"),
    BEARER("Bearer ");

    private final String value;
}
