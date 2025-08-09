package com.pfplaybackend.api.user.application.aspect.context;

import com.pfplaybackend.api.common.config.security.jwt.CustomJwtAuthenticationToken;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.config.security.jwt.dto.UserCredentials;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserContext {
    UserId userId;
    AuthorityTier authorityTier;

    public static UserContext create(CustomJwtAuthenticationToken token) {
        return new UserContext(token.getUserId(), token.getAuthorityTier());
    }
}
