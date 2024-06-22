package com.pfplaybackend.api.config.jwt.dto;

import com.pfplaybackend.api.user.domain.enums.AuthorityTier;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class UserCredentials {

    private final UserId userId;
    private final AuthorityTier authorityTier;

    public static UserCredentials create(UUID uid, AuthorityTier authorityTier) {
        return new UserCredentials(UserId.create(uid), authorityTier);
    }
}
