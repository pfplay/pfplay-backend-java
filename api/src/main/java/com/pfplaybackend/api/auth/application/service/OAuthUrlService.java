package com.pfplaybackend.api.auth.application.service;

import com.pfplaybackend.api.auth.application.port.out.StateStorePort;
import com.pfplaybackend.api.auth.adapter.in.web.dto.response.OAuthUrlResponse;
import com.pfplaybackend.api.auth.domain.enums.OAuthProvider;
import com.pfplaybackend.api.auth.adapter.out.external.config.OAuth2Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthUrlService {

    private final OAuth2Properties oAuth2Properties;
    private final StateStorePort stateStorePort;

    /**
     * OAuth 인증 URL 생성
     */
    public OAuthUrlResponse generateAuthUrl(OAuthProvider provider, String codeVerifier) {
        OAuth2Properties.Provider config = getProviderConfig(provider);

        // State 생성 및 Redis에 저장
        String state = stateStorePort.generateAndStoreState(provider.getValue());

        // Code Challenge 생성 (PKCE)
        String codeChallenge = generateCodeChallenge(codeVerifier);

        // OAuth URL 구성
        String authUrl = buildAuthUrl(provider, config, state, codeChallenge);

        log.debug("Generated OAuth URL for provider: {}", provider);

        return OAuthUrlResponse.builder()
                .authUrl(authUrl)
                .state(state)
                .provider(provider.getValue())
                .expiresIn(600L) // 10분 후 만료 (Redis TTL과 일치)
                .build();
    }

    /**
     * State 검증 및 제거
     */
    public boolean validateAndConsumeState(String state, OAuthProvider provider, String codeVerifier) {
        return stateStorePort.validateAndConsumeState(state, provider.getValue());
    }

    private String buildAuthUrl(OAuthProvider provider, OAuth2Properties.Provider config,
                                String state, String codeChallenge) {

        return switch (provider) {
            case GOOGLE -> buildGoogleAuthUrl(config, state, codeChallenge);
            case TWITTER -> buildTwitterAuthUrl(config, state, codeChallenge);
        };
    }

    private String buildGoogleAuthUrl(OAuth2Properties.Provider config, String state, String codeChallenge) {
        StringBuilder url = new StringBuilder();
        url.append(config.getAuthorizationUri()).append("?");
        url.append("client_id=").append(config.getClientId());
        url.append("&redirect_uri=").append(config.getRedirectUri());
        url.append("&response_type=code");
        url.append("&scope=").append(config.getScopesAsUrlEncoded());
        url.append("&state=").append(state);
        url.append("&code_challenge=").append(codeChallenge);
        url.append("&code_challenge_method=S256");

        // Google 전용 파라미터 (있는 경우만)
        if (config.getAccessType() != null) {
            url.append("&access_type=").append(config.getAccessType());
        }
        if (config.getPrompt() != null) {
            url.append("&prompt=").append(config.getPrompt());
        }

        return url.toString();
    }

    private String buildTwitterAuthUrl(OAuth2Properties.Provider config, String state, String codeChallenge) {
        StringBuilder url = new StringBuilder();
        url.append(config.getAuthorizationUri()).append("?");
        url.append("client_id=").append(config.getClientId());
        url.append("&redirect_uri=").append(config.getRedirectUri());
        url.append("&response_type=code");
        url.append("&scope=").append(config.getScopesAsUrlEncoded());
        url.append("&state=").append(state);
        url.append("&code_challenge=").append(codeChallenge);
        url.append("&code_challenge_method=S256");

        return url.toString();
    }

    private String generateCodeChallenge(String codeVerifier) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate code challenge", e);
        }
    }

    private OAuth2Properties.Provider getProviderConfig(OAuthProvider provider) {
        OAuth2Properties.Provider config = oAuth2Properties.getProviders().get(provider.getValue());
        if (config == null) {
            throw new IllegalArgumentException("Provider not configured: " + provider);
        }
        return config;
    }
}
