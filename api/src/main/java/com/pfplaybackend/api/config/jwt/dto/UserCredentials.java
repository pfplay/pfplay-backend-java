package com.pfplaybackend.api.config.jwt.dto;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.user.domain.value.UserId;
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
