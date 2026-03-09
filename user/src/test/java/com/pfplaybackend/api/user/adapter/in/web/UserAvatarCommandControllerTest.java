package com.pfplaybackend.api.user.adapter.in.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserAvatarCommandControllerTest extends AbstractUserWebMvcTest {

    @Test
    @DisplayName("setMyAvatar — 204 No Content")
    void setMyAvatarReturns204() throws Exception {
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
                .andExpect(status().isNoContent());
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
