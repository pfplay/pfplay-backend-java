package com.pfplaybackend.api.auth.application.service;

import com.pfplaybackend.api.auth.adapter.out.external.GoogleOAuthClient;
import com.pfplaybackend.api.auth.adapter.out.external.TwitterOAuthClient;
import com.pfplaybackend.api.auth.application.dto.oauth.OAuthTokenDto;
import com.pfplaybackend.api.auth.application.dto.oauth.OAuthUserProfileDto;
import com.pfplaybackend.api.auth.domain.enums.OAuthProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OAuthClientServiceTest {

    @Mock GoogleOAuthClient googleOAuthClient;
    @Mock TwitterOAuthClient twitterOAuthClient;
    @InjectMocks OAuthClientService oAuthClientService;

    @Test
    @DisplayName("exchangeCodeForToken — GOOGLE이면 GoogleClient에 위임한다")
    void exchangeCodeForTokenDelegatesToGoogle() {
        // given
        OAuthTokenDto expectedToken = new OAuthTokenDto("access", "Bearer", 3600, "refresh", "openid");
        when(googleOAuthClient.exchangeCodeForToken("code123", "verifier")).thenReturn(expectedToken);

        // when
        OAuthTokenDto result = oAuthClientService.exchangeCodeForToken(OAuthProvider.GOOGLE, "code123", "verifier");

        // then
        assertThat(result).isEqualTo(expectedToken);
        verify(googleOAuthClient).exchangeCodeForToken("code123", "verifier");
    }

    @Test
    @DisplayName("exchangeCodeForToken — TWITTER이면 TwitterClient에 위임한다")
    void exchangeCodeForTokenDelegatesToTwitter() {
        // given
        OAuthTokenDto expectedToken = new OAuthTokenDto("tw-access", "Bearer", 7200, "tw-refresh", "tweet.read");
        when(twitterOAuthClient.exchangeCodeForToken("twCode", "twVerifier")).thenReturn(expectedToken);

        // when
        OAuthTokenDto result = oAuthClientService.exchangeCodeForToken(OAuthProvider.TWITTER, "twCode", "twVerifier");

        // then
        assertThat(result).isEqualTo(expectedToken);
        verify(twitterOAuthClient).exchangeCodeForToken("twCode", "twVerifier");
    }

    @Test
    @DisplayName("getUserProfile — 올바른 클라이언트에 위임한다")
    void getUserProfileDelegatesToCorrectClient() {
        // given
        OAuthUserProfileDto expectedProfile = new OAuthUserProfileDto("gid123", "test@gmail.com", "Test User", "pic.jpg");
        when(googleOAuthClient.getUserProfile("gToken")).thenReturn(expectedProfile);

        // when
        OAuthUserProfileDto result = oAuthClientService.getUserProfile(OAuthProvider.GOOGLE, "gToken");

        // then
        assertThat(result).isEqualTo(expectedProfile);
        verify(googleOAuthClient).getUserProfile("gToken");
    }
}
