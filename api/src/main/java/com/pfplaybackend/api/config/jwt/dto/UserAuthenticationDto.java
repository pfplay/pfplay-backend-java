package com.pfplaybackend.api.config.jwt.dto;

import com.pfplaybackend.api.user.model.enums.AuthorityTier;
import com.pfplaybackend.api.user.model.value.UserId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class UserAuthenticationDto {

    private final UserId userId;
    private final AuthorityTier authorityTier;

    public static UserAuthenticationDto create(UUID uid, AuthorityTier authorityTier) {
        return new UserAuthenticationDto(UserId.create(uid), authorityTier);
    }
}
