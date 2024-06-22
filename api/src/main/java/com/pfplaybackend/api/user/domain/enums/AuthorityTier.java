package com.pfplaybackend.api.user.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthorityTier {
    FM("FULL_MEMBER"),
    AM("ASSOCIATE_MEMBER"),
    GT("GUEST");

    private final String description;

    public static AuthorityTier findByDescription(String value) {
        for (AuthorityTier authorityTier : AuthorityTier.values()) {
            if (authorityTier.getDescription().equals(value)) {
                return authorityTier;
            }
        }
        return null;
    }
}
