package com.pfplaybackend.api.auth.adapter.in.web;

import com.pfplaybackend.api.auth.application.dto.result.OAuthUrlResult;
import com.pfplaybackend.api.auth.application.service.AuthService;
import com.pfplaybackend.api.auth.application.service.LogoutService;
import com.pfplaybackend.api.auth.application.service.OAuthUrlService;
import com.pfplaybackend.api.auth.domain.enums.OAuthProvider;
import com.pfplaybackend.api.common.config.security.jwt.CookieUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean OAuthUrlService oAuthUrlService;
    @MockBean AuthService authService;
    @MockBean LogoutService logoutService;
    @MockBean CookieUtil cookieUtil;
    @MockBean JwtDecoder jwtDecoder;

    @Test
    @DisplayName("generateOAuthUrl — 200 OK + URL 반환")
    void generateOAuthUrl_returns200() throws Exception {
        // given
        String codeVerifier = "a".repeat(43);
        String body = """
                {
                    "provider": "google",
                    "codeVerifier": "%s"
                }
                """.formatted(codeVerifier);
        when(oAuthUrlService.generateAuthUrl(any(OAuthProvider.class), anyString()))
                .thenReturn(new OAuthUrlResult("https://accounts.google.com/auth", "state123", "google", 300L));

        // when & then
        mockMvc.perform(post("/api/v1/auth/oauth/url")
                        .with(jwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authUrl").value("https://accounts.google.com/auth"));
    }

    @Test
    @DisplayName("logout — 200 OK")
    void logout_returns200() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout")
                        .with(jwt().authorities(() -> "ROLE_MEMBER"))
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}
