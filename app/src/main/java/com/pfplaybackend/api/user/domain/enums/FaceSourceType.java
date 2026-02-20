package com.pfplaybackend.api.user.domain.enums;

import lombok.Getter;

@Getter
public enum FaceSourceType {
    INTERNAL_IMAGE("시스템 내부 리소스 URI"),
    NFT_URI("외부에서 입력된 사용자 지정 URI (e.g. NFT)");

    private final String description;

    FaceSourceType(String description) {
        this.description = description;
    }
}
