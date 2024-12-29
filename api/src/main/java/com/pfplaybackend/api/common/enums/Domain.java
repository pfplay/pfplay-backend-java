package com.pfplaybackend.api.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Domain {
    CLIENT("https://pfplay.xyz/");
    private final String url;
}
