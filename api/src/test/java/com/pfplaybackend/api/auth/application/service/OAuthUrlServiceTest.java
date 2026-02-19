package com.pfplaybackend.api.auth.application.service;

import com.pfplaybackend.api.auth.application.port.out.StateStorePort;
import com.pfplaybackend.api.auth.adapter.in.web.dto.response.OAuthUrlResponse;
import com.pfplaybackend.api.auth.domain.enums.OAuthProvider;
import com.pfplaybackend.api.auth.adapter.out.external.config.OAuth2Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
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
    void generateAuthUrl_shouldUseStateStore() {
        // given
        when(oAuth2Properties.getProviders()).thenReturn(Map.of("google", googleConfig));
        when(stateStorePort.generateAndStoreState("google")).thenReturn("test-state");

        // when
        OAuthUrlResponse response = oAuthUrlService.generateAuthUrl(OAuthProvider.GOOGLE, "test-verifier");

        // then
        verify(stateStorePort, times(1)).generateAndStoreState("google");
        assertThat(response.getState()).isEqualTo("test-state");
        assertThat(response.getAuthUrl()).contains("state=test-state");
    }

    @Test
    @DisplayName("validateAndConsumeState - StateStore를 통해 검증해야 한다")
    void validateAndConsumeState_shouldDelegateToStateStore() {
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
    void validateAndConsumeState_shouldReturnFalse_whenInvalid() {
        // given
        when(stateStorePort.validateAndConsumeState("invalid-state", "google")).thenReturn(false);

        // when
        boolean result = oAuthUrlService.validateAndConsumeState("invalid-state", OAuthProvider.GOOGLE, "verifier");

        // then
        assertThat(result).isFalse();
    }
}
