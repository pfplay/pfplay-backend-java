package com.pfplaybackend.api.user.domain.service;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import org.springframework.stereotype.Service;

@Service
public class UserDomainService {
    public boolean isGuest(AuthContext authContext) {
        return authContext.getAuthorityTier() == AuthorityTier.GT;
    }

    public boolean isGuest(AuthorityTier authorityTier) {
        return authorityTier == AuthorityTier.GT;
    }
}