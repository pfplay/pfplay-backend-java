package com.pfplaybackend.api.profile.domain.enums;

import lombok.Getter;

@Getter
public enum ProfileStatus {
    INCOMPLETE("프로필 미완성"),
    COMPLETE("프로필 완성"),
    SUSPENDED("프로필 정지"),
    DELETED("프로필 삭제됨");

    private final String description;

    ProfileStatus(String description) {
        this.description = description;
    }
}
