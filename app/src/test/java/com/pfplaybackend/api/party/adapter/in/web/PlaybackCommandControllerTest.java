package com.pfplaybackend.api.party.adapter.in.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PlaybackCommandControllerTest extends AbstractPartyCommandWebMvcTest {

    @Test
    @DisplayName("playBackSkip — 204 No Content")
    void playBackSkipReturns204() throws Exception {
        mockMvc.perform(delete("/api/v1/partyrooms/1/playbacks/current")
                        .with(jwt().authorities(() -> "ROLE_MEMBER"))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("playBackSkip — 인증 없으면 401")
    void playBackSkipUnauthenticatedReturns401() throws Exception {
        mockMvc.perform(delete("/api/v1/partyrooms/1/playbacks/current")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}
