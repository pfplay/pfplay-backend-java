package com.pfplaybackend.api.playlist.adapter.in.web;

import com.pfplaybackend.api.playlist.application.service.TrackCommandService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrackCommandController.class)
class TrackCommandControllerTest {

    private static final String ROLE_MEMBER = "ROLE_MEMBER";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TrackCommandService trackCommandService;

    @MockBean
    JwtDecoder jwtDecoder;

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
    @DisplayName("DELETE /{playlistId}/tracks/{trackId} — 인증되면 202를 반환한다")
    void deleteTrackAuthenticatedReturns202() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/v1/playlists/1/tracks/10")
                        .with(jwt().authorities(() -> ROLE_MEMBER))
                        .with(csrf()))
                .andExpect(status().isAccepted());
    }

    @Test
    @DisplayName("PUT /{playlistId}/tracks/{trackId} — 인증 + 순서 변경 요청이면 202를 반환한다")
    void updateTrackOrderAuthenticatedReturns202() throws Exception {
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
                .andExpect(status().isAccepted());
    }
}
