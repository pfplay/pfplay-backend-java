package com.pfplaybackend.api.profile.domain.enums;

import lombok.Getter;

@Getter
public enum AvatarCompositionType {
    SINGLE_BODY("단일 body 이미지로 구성"),
    BODY_WITH_FACE("body + face 조합 구성");

    private final String description;

    AvatarCompositionType(String description) {
        this.description = description;
    }
}