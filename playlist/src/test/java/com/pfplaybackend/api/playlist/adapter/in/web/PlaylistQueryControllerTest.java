package com.pfplaybackend.api.playlist.adapter.in.web;

import com.pfplaybackend.api.playlist.application.dto.PlaylistSummaryDto;
import com.pfplaybackend.api.playlist.application.service.PlaylistQueryService;
import com.pfplaybackend.api.playlist.domain.enums.PlaylistType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlaylistQueryController.class)
class PlaylistQueryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PlaylistQueryService playlistQueryService;

    @MockBean
    JwtDecoder jwtDecoder;

    @Test
    @DisplayName("GET /playlists — MEMBER 권한이면 200과 플레이리스트 배열을 반환한다")
    void getPlaylists_member_returns200() throws Exception {
        // given
        List<PlaylistSummaryDto> playlists = List.of(
                new PlaylistSummaryDto(1L, "My Playlist", 1, PlaylistType.PLAYLIST, 5L),
                new PlaylistSummaryDto(2L, "Grab List", 2, PlaylistType.GRABLIST, 3L)
        );
        when(playlistQueryService.getPlaylists()).thenReturn(playlists);

        // when & then
        mockMvc.perform(get("/api/v1/playlists")
                        .with(jwt().authorities(() -> "ROLE_MEMBER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.playlists").isArray())
                .andExpect(jsonPath("$.data.playlists.length()").value(2))
                .andExpect(jsonPath("$.data.playlists[0].name").value("My Playlist"));
    }

    @Test
    @DisplayName("GET /playlists — 미인증이면 401을 반환한다")
    void getPlaylists_unauthenticated_returns401() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/playlists"))
                .andExpect(status().isUnauthorized());
    }
}
