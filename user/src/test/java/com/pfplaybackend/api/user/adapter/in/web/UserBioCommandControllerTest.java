package com.pfplaybackend.api.user.adapter.in.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserBioCommandControllerTest extends AbstractUserWebMvcTest {

    private static final String BIO_ENDPOINT = "/api/v1/users/me/profile/bio";

    @Test
    @DisplayName("PUT /me/profile/bio — MEMBER 권한 + 유효한 요청이면 204를 반환한다")
    void setMyBioMemberReturns204() throws Exception {
        // given
        String body = """
                {"nickname": "NewNick", "introduction": "Hello World"}
                """;

        // when & then
        mockMvc.perform(put(BIO_ENDPOINT)
                        .with(jwt().authorities(() -> "ROLE_MEMBER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("PUT /me/profile/bio — 미인증이면 401을 반환한다")
    void setMyBioUnauthenticatedReturns401() throws Exception {
        // given
        String body = """
                {"nickname": "NewNick", "introduction": "Hello"}
                """;

        // when & then
        mockMvc.perform(put(BIO_ENDPOINT)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PUT /me/profile/bio — GUEST 권한이면 403을 반환한다")
    void setMyBioGuestReturns403() throws Exception {
        // given
        String body = """
                {"nickname": "NewNick", "introduction": "Hello"}
                """;

        // when & then
        mockMvc.perform(put(BIO_ENDPOINT)
                        .with(jwt().authorities(() -> "ROLE_GUEST"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }
}
