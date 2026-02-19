package com.pfplaybackend.api.auth.application.service;

import com.mysema.commons.lang.Assert;
import com.pfplaybackend.api.auth.application.store.StateStore;
import com.pfplaybackend.api.auth.dto.request.OAuthLoginRequest;
import com.pfplaybackend.api.auth.dto.response.AuthResponse;
import com.pfplaybackend.api.auth.enums.OAuthProvider;
import com.pfplaybackend.api.common.config.security.jwt.JwtService;
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
    private final StateStore stateStore;

    @Transactional
    public AuthResponse processOAuthLogin(OAuthLoginRequest request) {

        try {
            OAuthProvider provider = OAuthProvider.fromString(request.getProvider());
            log.debug("Processing OAuth login for provider: {}", provider);

            // 1. Exchange authorization code for access token
            var tokenResponse = oAuthClientService.exchangeCodeForToken(
                    provider,
                    request.getCode(),
                    request.getCodeVerifier()
            );

            // 2. Get user profile from OAuth provider
            var userProfile = oAuthClientService.getUserProfile(
                    provider,
                    tokenResponse.getAccessToken()
            );

            // 3. Get or Create Member
            MemberData member = memberSignService.getMemberOrCreate(userProfile.getEmail());

            // 4. Generate JWT tokens
            String accessToken = jwtService.generateAccessTokenForMember(member);

            // 5. Build response
            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .tokenType("Cookie")
                    .expiresIn(jwtService.getAccessTokenExpiration())
                    .issuedAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("OAuth login failed: {}", e.getMessage(), e);
            throw new AuthenticationException("Authentication failed: " + e.getMessage());
        }
    }

    public boolean validateToken(String token) {
        return jwtService.validateAccessToken(token);
    }
}