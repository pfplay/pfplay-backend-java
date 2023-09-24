package com.pfplaybackend.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Domain {
    CLIENT("https://pfplay.io/");
    private final String url;
}