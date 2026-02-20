package com.pfplaybackend.api.common.config.security.jwt.dto;

import com.pfplaybackend.api.common.config.security.enums.AccessLevel;
import com.pfplaybackend.api.common.enums.AuthorityTier;

public record TokenClaimsRequest(
        String uid,
        String email,
        AccessLevel accessLevel,
        AuthorityTier authorityTier
) {
}
