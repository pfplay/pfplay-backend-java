package com.pfplaybackend.api.auth.application.service;

import com.pfplaybackend.api.auth.dto.response.OAuthUrlResponse;
import com.pfplaybackend.api.auth.enums.OAuthProvider;
import com.pfplaybackend.api.common.config.security.jwt.properties.OAuth2Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthUrlService {

    private final OAuth2Properties oAuth2Properties;

    // State 저장소 (실제 환경에서는 Redis 등 사용 권장)
    private final ConcurrentHashMap<String, StateInfo> stateStore = new ConcurrentHashMap<>();

    private static final SecureRandom secureRandom = new SecureRandom();

    /**
     * OAuth 인증 URL 생성
     */
    public OAuthUrlResponse generateAuthUrl(OAuthProvider provider, String codeVerifier) {
        OAuth2Properties.Provider config = getProviderConfig(provider);

        // State 생성 및 저장
        String state = generateState();
        storeState(state, provider, codeVerifier);

        // Code Challenge 생성 (PKCE)
        String codeChallenge = generateCodeChallenge(codeVerifier);

        // OAuth URL 구성
        String authUrl = buildAuthUrl(provider, config, state, codeChallenge);

        log.debug("Generated OAuth URL for provider: {}", provider);

        return OAuthUrlResponse.builder()
                .authUrl(authUrl)
                .state(state)
                .provider(provider.getValue())
                .expiresIn(300L) // 5분 후 만료
                .build();
    }

    /**
     * State 검증 및 제거
     */
    public boolean validateAndConsumeState(String state, OAuthProvider provider, String codeVerifier) {
        StateInfo stateInfo = stateStore.remove(state);

        if (stateInfo == null) {
            log.warn("Invalid or expired state: {}", state);
            return false;
        }

        // 만료 시간 확인
        if (System.currentTimeMillis() > stateInfo.getExpiresAt()) {
            log.warn("Expired state: {}", state);
            return false;
        }

        // Provider 확인
        if (!stateInfo.getProvider().equals(provider)) {
            log.warn("Provider mismatch for state: {} - expected: {}, actual: {}",
                    state, stateInfo.getProvider(), provider);
            return false;
        }

        // Code verifier 확인 (선택적)
        if (codeVerifier != null && !codeVerifier.equals(stateInfo.getCodeVerifier())) {
            log.warn("Code verifier mismatch for state: {}", state);
            return false;
        }

        return true;
    }

    /**
     * 만료된 state 정리 (스케줄러에서 호출)
     */
    public void cleanupExpiredStates() {
        long now = System.currentTimeMillis();
        stateStore.entrySet().removeIf(entry -> entry.getValue().getExpiresAt() < now);
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

    private String generateState() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String generateCodeChallenge(String codeVerifier) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(codeVerifier.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate code challenge", e);
        }
    }

    private void storeState(String state, OAuthProvider provider, String codeVerifier) {
        long expiresAt = System.currentTimeMillis() + (5 * 60 * 1000); // 5분
        StateInfo stateInfo = new StateInfo(provider, codeVerifier, expiresAt);
        stateStore.put(state, stateInfo);
    }

    private OAuth2Properties.Provider getProviderConfig(OAuthProvider provider) {
        OAuth2Properties.Provider config = oAuth2Properties.getProviders().get(provider.getValue());
        if (config == null) {
            throw new IllegalArgumentException("Provider not configured: " + provider);
        }
        return config;
    }

    /**
     * State 정보를 저장하는 내부 클래스
     */
    private static class StateInfo {
        private final OAuthProvider provider;
        private final String codeVerifier;
        private final long expiresAt;

        public StateInfo(OAuthProvider provider, String codeVerifier, long expiresAt) {
            this.provider = provider;
            this.codeVerifier = codeVerifier;
            this.expiresAt = expiresAt;
        }

        public OAuthProvider getProvider() { return provider; }
        public String getCodeVerifier() { return codeVerifier; }
        public long getExpiresAt() { return expiresAt; }
    }
}