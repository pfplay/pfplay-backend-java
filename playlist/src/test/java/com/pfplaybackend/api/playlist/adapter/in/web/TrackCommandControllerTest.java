package com.pfplaybackend.api.playlist.adapter.in.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TrackCommandControllerTest extends AbstractPlaylistWebMvcTest {

    private static final String ROLE_MEMBER = "ROLE_MEMBER";

    @Test
    @DisplayName("POST /{playlistId}/tracks — MEMBER 권한이면 201을 반환한다")
    void addTrackMemberReturns201() throws Exception {
        // given
        String body = """
                {
                    "name": "BLACKPINK - Shut Down",
                    "linkId": "POe9SOEKotk",
                    "duration": "03:01",
                    "thumbnailImage": "https://i.ytimg.com/vi/POe9SOEKotk/mqdefault.jpg"
                }
                """;

        // when & then
        mockMvc.perform(post("/api/v1/playlists/1/tracks")
                        .with(jwt().authorities(() -> ROLE_MEMBER))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /{playlistId}/tracks — 미인증이면 401을 반환한다")
    void addTrackUnauthenticatedReturns401() throws Exception {
        // given
        String body = """
                {
                    "name": "Test Track",
                    "linkId": "abc123",
                    "duration": "03:00",
                    "thumbnailImage": "https://example.com/thumb.jpg"
                }
                """;

        // when & then
        mockMvc.perform(post("/api/v1/playlists/1/tracks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE /{playlistId}/tracks/{trackId} — 인증되면 204를 반환한다")
    void deleteTrackAuthenticatedReturns204() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/v1/playlists/1/tracks/10")
                        .with(jwt().authorities(() -> ROLE_MEMBER))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("PUT /{playlistId}/tracks/{trackId} — 인증 + 순서 변경 요청이면 204를 반환한다")
    void updateTrackOrderAuthenticatedReturns204() throws Exception {
        // given
        String body = """
                {"nextOrderNumber": 3}
                """;

        // when & then
        mockMvc.perform(put("/api/v1/playlists/1/tracks/5")
                        .with(jwt().authorities(() -> ROLE_MEMBER))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent());
    }
}
