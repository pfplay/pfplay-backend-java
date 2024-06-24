package com.pfplaybackend.api.user.domain.service;

import com.pfplaybackend.api.config.jwt.dto.UserCredentials;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.user.application.aspect.context.UserContext;
import org.springframework.stereotype.Service;

@Service
public class UserDomainService {
    public boolean isGuest(UserContext userContext) {
        return userContext.getAuthorityTier() == AuthorityTier.GT;
    }
}