package com.pfplaybackend.api.party.adapter.in.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PlaybackReactionCommandControllerTest extends AbstractPartyCommandWebMvcTest {

    @Test
    @DisplayName("reactToPlayback — 200 OK")
    void reactToPlaybackReturns200() throws Exception {
        // given
        String body = """
                {
                    "reactionType": "LIKE"
                }
                """;
        when(playbackReactionCommandService.reactToCurrentPlayback(any(), any()))
                .thenReturn(Map.of("liked", true, "disliked", false, "grabbed", false));

        // when & then
        mockMvc.perform(post("/api/v1/partyrooms/1/playbacks/reaction")
                        .with(jwt().authorities(() -> "ROLE_MEMBER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("reactToPlayback — 인증 없으면 401")
    void reactToPlaybackUnauthenticatedReturns401() throws Exception {
        String body = """
                {
                    "reactionType": "LIKE"
                }
                """;

        mockMvc.perform(post("/api/v1/partyrooms/1/playbacks/reaction")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }
}
