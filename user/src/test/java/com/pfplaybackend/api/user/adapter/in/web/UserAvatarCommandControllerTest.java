package com.pfplaybackend.api.user.adapter.in.web;

import com.pfplaybackend.api.user.application.service.UserAvatarCommandService;
import com.pfplaybackend.api.user.application.validation.AvatarRequestValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserAvatarCommandController.class)
@Import(UserAvatarCommandControllerTest.TestMethodSecurityConfig.class)
class UserAvatarCommandControllerTest {

    @EnableMethodSecurity
    static class TestMethodSecurityConfig {}

    @Autowired MockMvc mockMvc;
    @MockBean UserAvatarCommandService userAvatarCommandService;
    @MockBean AvatarRequestValidator avatarRequestValidator;
    @MockBean JwtDecoder jwtDecoder;

    @Test
    @DisplayName("setMyAvatar — 200 OK")
    void setMyAvatarReturns200() throws Exception {
        String body = """
                {
                    "avatarCompositionType": "SINGLE_BODY",
                    "body": {
                        "uri": "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_basic%2Fava_basic_001.png?alt=media"
                    }
                }
                """;

        mockMvc.perform(put("/api/v1/users/me/profile/avatar")
                        .with(jwt().authorities(() -> "ROLE_MEMBER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("setMyAvatar — 인증 없으면 401")
    void setMyAvatarUnauthenticatedReturns401() throws Exception {
        String body = """
                {
                    "avatarCompositionType": "SINGLE_BODY",
                    "body": {
                        "uri": "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_basic%2Fava_basic_001.png?alt=media"
                    }
                }
                """;

        mockMvc.perform(put("/api/v1/users/me/profile/avatar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }
}
