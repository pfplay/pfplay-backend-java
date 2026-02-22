package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.party.application.service.PlaybackCommandService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlaybackCommandController.class)
class PlaybackCommandControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean PlaybackCommandService playbackCommandService;
    @MockBean JwtDecoder jwtDecoder;

    @Test
    @DisplayName("playBackSkip — 200 OK")
    void playBackSkip_returns200() throws Exception {
        mockMvc.perform(post("/api/v1/partyrooms/1/playbacks/skip")
                        .with(jwt().authorities(() -> "ROLE_MEMBER"))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("playBackSkip — 인증 없으면 401")
    void playBackSkip_unauthenticated_returns401() throws Exception {
        mockMvc.perform(post("/api/v1/partyrooms/1/playbacks/skip")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}
