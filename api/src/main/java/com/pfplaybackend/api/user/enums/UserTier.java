package com.pfplaybackend.api.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserTier {
    FM("FULL_MEMBER"),
    AM("ASSOCIATE_MEMBER"),
    GT("GUEST");

    private final String description;

    public static UserTier findByDescription(String value) {
        for (UserTier userTier : UserTier.values()) {
            if (userTier.getDescription().equals(value)) {
                return userTier;
            }
        }
        return null;
    }
}
