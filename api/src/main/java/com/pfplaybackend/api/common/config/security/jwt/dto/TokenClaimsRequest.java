package com.pfplaybackend.api.common.config.security.jwt.dto;

import com.pfplaybackend.api.common.config.security.enums.AccessLevel;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TokenClaimsRequest {
    private String uid;
    private String email;
    private AccessLevel accessLevel;
    private AuthorityTier authorityTier;
}
