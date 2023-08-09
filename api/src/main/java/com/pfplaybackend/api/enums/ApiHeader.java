package com.pfplaybackend.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApiHeader {
    AUTHORIZATION("Authorization"),
    BEARER("Bearer ");

    private final String value;
}
