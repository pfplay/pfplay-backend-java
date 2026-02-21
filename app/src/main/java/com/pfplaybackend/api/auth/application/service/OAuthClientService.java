package com.pfplaybackend.api.auth.application.service;

import com.pfplaybackend.api.auth.adapter.out.external.GoogleOAuthClient;
import com.pfplaybackend.api.auth.adapter.out.external.TwitterOAuthClient;
import com.pfplaybackend.api.auth.application.dto.oauth.OAuthTokenDto;
import com.pfplaybackend.api.auth.application.dto.oauth.OAuthUserProfileDto;
import com.pfplaybackend.api.auth.domain.enums.OAuthProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthClientService {

    private final GoogleOAuthClient googleOAuthClient;
    private final TwitterOAuthClient twitterOAuthClient;

    public OAuthTokenDto exchangeCodeForToken(OAuthProvider provider, String code, String codeVerifier) {
        log.debug("Exchanging code for token with provider: {}", provider);

        return switch (provider) {
            case GOOGLE -> googleOAuthClient.exchangeCodeForToken(code, codeVerifier);
            case TWITTER -> twitterOAuthClient.exchangeCodeForToken(code, codeVerifier);
        };
    }

    public OAuthUserProfileDto getUserProfile(OAuthProvider provider, String accessToken) {
        log.debug("Fetching user profile from provider: {}", provider);

        return switch (provider) {
            case GOOGLE -> googleOAuthClient.getUserProfile(accessToken);
            case TWITTER -> twitterOAuthClient.getUserProfile(accessToken);
        };
    }
}
