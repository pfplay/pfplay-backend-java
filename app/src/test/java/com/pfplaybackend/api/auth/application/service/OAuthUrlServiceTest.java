package com.pfplaybackend.api.auth.application.service;

import com.pfplaybackend.api.auth.application.port.out.StateStorePort;
import com.pfplaybackend.api.auth.application.dto.result.OAuthUrlResult;
import com.pfplaybackend.api.auth.domain.enums.OAuthProvider;
import com.pfplaybackend.api.auth.adapter.out.external.config.OAuth2Properties;
import com.pfplaybackend.api.common.exception.http.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuthUrlServiceTest {

    @Mock private OAuth2Properties oAuth2Properties;
    @Mock private StateStorePort stateStorePort;

    @InjectMocks
    private OAuthUrlService oAuthUrlService;

    private OAuth2Properties.Provider googleConfig;

    @BeforeEach
    void setUp() {
        googleConfig = new OAuth2Properties.Provider();
        googleConfig.setClientId("test-client-id");
        googleConfig.setRedirectUri("http://localhost/callback");
        googleConfig.setAuthorizationUri("https://accounts.google.com/o/oauth2/auth");
        googleConfig.setScopes(java.util.List.of("openid", "email", "profile"));
    }

    @Test
    @DisplayName("generateAuthUrl - StateStore를 통해 state를 생성해야 한다")
    void generateAuthUrlShouldUseStateStore() {
        // given
        when(oAuth2Properties.getProviders()).thenReturn(Map.of("google", googleConfig));
        when(stateStorePort.generateAndStoreState("google")).thenReturn("test-state");

        // when
        OAuthUrlResult result = oAuthUrlService.generateAuthUrl(OAuthProvider.GOOGLE, "test-verifier");

        // then
        verify(stateStorePort, times(1)).generateAndStoreState("google");
        assertThat(result.state()).isEqualTo("test-state");
        assertThat(result.authUrl()).contains("state=test-state");
    }

    @Test
    @DisplayName("validateAndConsumeState - StateStore를 통해 검증해야 한다")
    void validateAndConsumeStateShouldDelegateToStateStore() {
        // given
        when(stateStorePort.validateAndConsumeState("test-state", "google")).thenReturn(true);

        // when
        boolean result = oAuthUrlService.validateAndConsumeState("test-state", OAuthProvider.GOOGLE, "verifier");

        // then
        verify(stateStorePort, times(1)).validateAndConsumeState("test-state", "google");
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("validateAndConsumeState - 잘못된 state는 false를 반환해야 한다")
    void validateAndConsumeStateShouldReturnFalseWhenInvalid() {
        // given
        when(stateStorePort.validateAndConsumeState("invalid-state", "google")).thenReturn(false);

        // when
        boolean result = oAuthUrlService.validateAndConsumeState("invalid-state", OAuthProvider.GOOGLE, "verifier");

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("generateAuthUrl — Twitter URL이 올바르게 생성된다")
    void generateAuthUrlTwitterBuildsCorrectUrl() {
        // given
        OAuth2Properties.Provider twitterConfig = new OAuth2Properties.Provider();
        twitterConfig.setClientId("twitter-client-id");
        twitterConfig.setRedirectUri("http://localhost/twitter/callback");
        twitterConfig.setAuthorizationUri("https://twitter.com/i/oauth2/authorize");
        twitterConfig.setScopes(java.util.List.of("tweet.read", "users.read"));

        when(oAuth2Properties.getProviders()).thenReturn(Map.of("twitter", twitterConfig));
        when(stateStorePort.generateAndStoreState("twitter")).thenReturn("twitter-state");

        // when
        OAuthUrlResult result = oAuthUrlService.generateAuthUrl(OAuthProvider.TWITTER, "test-verifier");

        // then
        assertThat(result.authUrl()).contains("https://twitter.com/i/oauth2/authorize");
        assertThat(result.authUrl()).contains("client_id=twitter-client-id");
        assertThat(result.authUrl()).contains("state=twitter-state");
        assertThat(result.authUrl()).contains("code_challenge=");
        assertThat(result.authUrl()).contains("code_challenge_method=S256");
        assertThat(result.provider()).isEqualTo("twitter");
    }

    @Test
    @DisplayName("generateAuthUrl — Google에 access_type과 prompt가 포함된다")
    void generateAuthUrlGoogleWithAccessTypeAndPrompt() {
        // given
        googleConfig.setAccessType("offline");
        googleConfig.setPrompt("consent");

        when(oAuth2Properties.getProviders()).thenReturn(Map.of("google", googleConfig));
        when(stateStorePort.generateAndStoreState("google")).thenReturn("google-state");

        // when
        OAuthUrlResult result = oAuthUrlService.generateAuthUrl(OAuthProvider.GOOGLE, "test-verifier");

        // then
        assertThat(result.authUrl()).contains("access_type=offline");
        assertThat(result.authUrl()).contains("prompt=consent");
    }

    @Test
    @DisplayName("generateAuthUrl — Google에 access_type/prompt가 null이면 포함되지 않는다")
    void generateAuthUrlGoogleNullAccessTypePromptExcludedFromUrl() {
        // given
        googleConfig.setAccessType(null);
        googleConfig.setPrompt(null);

        when(oAuth2Properties.getProviders()).thenReturn(Map.of("google", googleConfig));
        when(stateStorePort.generateAndStoreState("google")).thenReturn("google-state");

        // when
        OAuthUrlResult result = oAuthUrlService.generateAuthUrl(OAuthProvider.GOOGLE, "test-verifier");

        // then
        assertThat(result.authUrl()).doesNotContain("access_type=");
        assertThat(result.authUrl()).doesNotContain("prompt=");
    }

    @Test
    @DisplayName("generateAuthUrl — 설정되지 않은 provider이면 예외가 발생한다")
    void generateAuthUrlUnconfiguredProviderThrows() {
        // given
        when(oAuth2Properties.getProviders()).thenReturn(Map.of());

        // when & then
        assertThatThrownBy(() -> oAuthUrlService.generateAuthUrl(OAuthProvider.GOOGLE, "verifier"))
                .isInstanceOf(BadRequestException.class);
    }
}
