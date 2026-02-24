package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.party.application.service.DjCommandService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DjCommandController.class)
@Import(DjCommandControllerTest.TestMethodSecurityConfig.class)
class DjCommandControllerTest {

    @EnableMethodSecurity
    static class TestMethodSecurityConfig {}

    @Autowired MockMvc mockMvc;
    @MockBean DjCommandService djCommandService;
    @MockBean JwtDecoder jwtDecoder;

    @Test
    @DisplayName("enqueueDj — 200 OK")
    void enqueueDjReturns200() throws Exception {
        String body = """
                {
                    "playlistId": 1
                }
                """;

        mockMvc.perform(post("/api/v1/partyrooms/1/djs")
                        .with(jwt().authorities(() -> "ROLE_MEMBER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("dequeueDj — 200 OK")
    void dequeueDjReturns200() throws Exception {
        mockMvc.perform(delete("/api/v1/partyrooms/1/djs/me")
                        .with(jwt().authorities(() -> "ROLE_MEMBER"))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("enqueueDj — 인증 없으면 401")
    void enqueueDjUnauthenticatedReturns401() throws Exception {
        String body = """
                {
                    "playlistId": 1
                }
                """;

        mockMvc.perform(post("/api/v1/partyrooms/1/djs")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }
}
