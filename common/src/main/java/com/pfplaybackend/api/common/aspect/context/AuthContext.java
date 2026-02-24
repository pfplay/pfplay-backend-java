package com.pfplaybackend.api.common.aspect.context;

import com.pfplaybackend.api.common.config.security.jwt.CustomJwtAuthenticationToken;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthContext {
    UserId userId;
    AuthorityTier authorityTier;

    public static AuthContext create(CustomJwtAuthenticationToken token) {
        return new AuthContext(token.getUserId(), token.getAuthorityTier());
    }
}
