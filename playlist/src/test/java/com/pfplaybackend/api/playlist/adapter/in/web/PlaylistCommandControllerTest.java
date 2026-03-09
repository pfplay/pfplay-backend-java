package com.pfplaybackend.api.playlist.adapter.in.web;

import com.pfplaybackend.api.playlist.domain.entity.data.PlaylistData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PlaylistCommandControllerTest extends AbstractPlaylistWebMvcTest {

    private static final String ROLE_MEMBER = "ROLE_MEMBER";

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
    @DisplayName("deletePlaylist — 204 No Content")
    void deletePlaylistReturns204() throws Exception {
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
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("modifyPlaylistName — 204 No Content")
    void modifyPlaylistNameReturns204() throws Exception {
        // given
        String body = """
                {
                    "name": "수정된 이름"
                }
                """;

        // when & then
        mockMvc.perform(patch("/api/v1/playlists/1")
                        .with(jwt().authorities(() -> ROLE_MEMBER))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent());
    }
}
