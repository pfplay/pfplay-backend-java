package com.pfplaybackend.api.common.config.security.jwt.dto;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class UserCredentials {

    private final UUID uid;
    private final AuthorityTier authorityTier;

    public static UserCredentials create(UUID uid, AuthorityTier authorityTier) {
        return new UserCredentials(uid, authorityTier);
    }
}
