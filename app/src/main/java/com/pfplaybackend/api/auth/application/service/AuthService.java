package com.pfplaybackend.api.auth.application.service;

import com.mysema.commons.lang.Assert;
import com.pfplaybackend.api.auth.application.port.out.StateStorePort;
import com.pfplaybackend.api.auth.application.dto.command.OAuthLoginCommand;
import com.pfplaybackend.api.auth.application.dto.result.AuthResult;
import com.pfplaybackend.api.auth.domain.enums.OAuthProvider;
import com.pfplaybackend.api.common.config.security.enums.ProviderType;
import com.pfplaybackend.api.common.config.security.enums.AccessLevel;
import com.pfplaybackend.api.common.config.security.jwt.JwtService;
import com.pfplaybackend.api.common.config.security.jwt.dto.TokenClaimsRequest;
import com.pfplaybackend.api.common.exception.AuthenticationException;
import com.pfplaybackend.api.user.application.service.MemberSignService;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final OAuthClientService oAuthClientService;
    private final MemberSignService memberSignService;
    private final JwtService jwtService;
    private final StateStorePort stateStorePort;

    @Transactional
    public AuthResult processOAuthLogin(OAuthLoginCommand command) {

        try {
            OAuthProvider provider = OAuthProvider.fromString(command.provider());
            log.debug("Processing OAuth login for provider: {}", provider);

            // 1. Exchange authorization code for access token
            var tokenResponse = oAuthClientService.exchangeCodeForToken(
                    provider,
                    command.code(),
                    command.codeVerifier()
            );

            // 2. Get user profile from OAuth provider
            var userProfile = oAuthClientService.getUserProfile(
                    provider,
                    tokenResponse.accessToken()
            );

            // 3. Get or Create Member
            ProviderType providerType = ProviderType.valueOf(provider.name());
            MemberData member = memberSignService.getMemberOrCreate(userProfile.email(), providerType);

            // 4. Generate JWT tokens
            String accessToken = jwtService.generateAccessToken(new TokenClaimsRequest(
                    member.getUserId().getUid().toString(),
                    member.getEmail(),
                    AccessLevel.ROLE_MEMBER,
                    member.getAuthorityTier()
            ));

            // 5. Build response
            return new AuthResult(accessToken, "Cookie", jwtService.getAccessTokenExpiration(), LocalDateTime.now());

        } catch (Exception e) {
            log.error("OAuth login failed: {}", e.getMessage(), e);
            throw new AuthenticationException("Authentication failed: " + e.getMessage());
        }
    }

    public boolean validateToken(String token) {
        return jwtService.validateAccessToken(token);
    }
}
