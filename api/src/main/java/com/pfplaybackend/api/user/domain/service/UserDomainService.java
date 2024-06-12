package com.pfplaybackend.api.user.domain.service;

import com.pfplaybackend.api.config.jwt.dto.UserCredentials;
import com.pfplaybackend.api.user.domain.model.enums.AuthorityTier;
import org.springframework.stereotype.Service;

@Service
public class UserDomainService {
    public boolean isGuest(UserCredentials userCredentials) {
        return userCredentials.getAuthorityTier() == AuthorityTier.GT;
    }
}