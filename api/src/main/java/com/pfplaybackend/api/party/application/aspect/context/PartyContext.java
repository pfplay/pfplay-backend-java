package com.pfplaybackend.api.party.application.aspect.context;

import com.pfplaybackend.api.common.config.security.jwt.CustomJwtAuthenticationToken;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.config.security.jwt.dto.UserCredentials;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PartyContext {
    UserId userId;
    AuthorityTier authorityTier;

    public static PartyContext create(CustomJwtAuthenticationToken token) {
        return new PartyContext(token.getUserId(), token.getAuthorityTier());
    }
}
