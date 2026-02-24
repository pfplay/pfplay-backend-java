package com.pfplaybackend.api.playlist.adapter.in.web;

import com.pfplaybackend.api.playlist.application.service.PlaylistCommandService;
import com.pfplaybackend.api.playlist.domain.entity.data.PlaylistData;
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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlaylistCommandController.class)
@Import(PlaylistCommandControllerTest.TestMethodSecurityConfig.class)
class PlaylistCommandControllerTest {

    private static final String ROLE_MEMBER = "ROLE_MEMBER";

    @EnableMethodSecurity
    static class TestMethodSecurityConfig {
    }

    @Autowired
    MockMvc mockMvc;
    @MockBean
    PlaylistCommandService playlistCommandService;
    @MockBean
    JwtDecoder jwtDecoder;

    @Test
    @DisplayName("create — 201 Created")
    void createReturns201() throws Exception {
        // given
        String body = """
                {
                    "name": "나의 플레이리스트"
                }
                """;
        when(playlistCommandService.createPlaylist(anyString()))
                .thenReturn(PlaylistData.builder().id(1L).name("나의 플레이리스트").orderNumber(1).build());

        // when & then
        mockMvc.perform(post("/api/v1/playlists")
                        .with(jwt().authorities(() -> ROLE_MEMBER))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("deletePlaylist — 200 OK")
    void deletePlaylistReturns200() throws Exception {
        String body = """
                {
                    "playlistIds": [1, 2]
                }
                """;

        mockMvc.perform(delete("/api/v1/playlists")
                        .with(jwt().authorities(() -> ROLE_MEMBER))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("modifyPlaylistName — 200 OK")
    void modifyPlaylistNameReturns200() throws Exception {
        // given
        String body = """
                {
                    "name": "수정된 이름"
                }
                """;
        when(playlistCommandService.renamePlaylist(anyLong(), anyString()))
                .thenReturn(PlaylistData.builder().id(1L).name("수정된 이름").orderNumber(1).build());

        // when & then
        mockMvc.perform(patch("/api/v1/playlists/1")
                        .with(jwt().authorities(() -> ROLE_MEMBER))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }
}
