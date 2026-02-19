package com.pfplaybackend.api.auth.application.service;

import com.pfplaybackend.api.auth.adapter.out.external.GoogleOAuthClient;
import com.pfplaybackend.api.auth.adapter.out.external.TwitterOAuthClient;
import com.pfplaybackend.api.auth.dto.response.OAuthTokenResponse;
import com.pfplaybackend.api.auth.dto.OAuthUserProfile;
import com.pfplaybackend.api.auth.enums.OAuthProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthClientService {

    private final GoogleOAuthClient googleOAuthClient;
    private final TwitterOAuthClient twitterOAuthClient;

    public OAuthTokenResponse exchangeCodeForToken(OAuthProvider provider, String code, String codeVerifier) {
        log.debug("Exchanging code for token with provider: {}", provider);

        return switch (provider) {
            case GOOGLE -> googleOAuthClient.exchangeCodeForToken(code, codeVerifier);
            case TWITTER -> twitterOAuthClient.exchangeCodeForToken(code, codeVerifier);
        };
    }

    public OAuthUserProfile getUserProfile(OAuthProvider provider, String accessToken) {
        log.debug("Fetching user profile from provider: {}", provider);

        return switch (provider) {
            case GOOGLE -> googleOAuthClient.getUserProfile(accessToken);
            case TWITTER -> twitterOAuthClient.getUserProfile(accessToken);
        };
    }
}