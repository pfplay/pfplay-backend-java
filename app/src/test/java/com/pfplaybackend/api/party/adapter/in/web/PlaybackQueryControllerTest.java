package com.pfplaybackend.api.party.adapter.in.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PlaybackQueryControllerTest extends AbstractPartyQueryWebMvcTest {

    @Test
    @DisplayName("playBackHistory — 200 OK")
    void playBackHistoryReturns200() throws Exception {
        // given
        when(playbackQueryService.getRecentPlaybackHistory(any())).thenReturn(List.of());

        // when & then
        mockMvc.perform(get("/api/v1/partyrooms/1/playbacks/histories")
                        .with(jwt().authorities(() -> "ROLE_MEMBER")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("playBackHistory — 인증 없으면 401")
    void playBackHistoryUnauthenticatedReturns401() throws Exception {
        mockMvc.perform(get("/api/v1/partyrooms/1/playbacks/histories"))
                .andExpect(status().isUnauthorized());
    }
}
