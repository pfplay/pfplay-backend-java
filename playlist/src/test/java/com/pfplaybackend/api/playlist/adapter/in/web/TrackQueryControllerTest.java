package com.pfplaybackend.api.playlist.adapter.in.web;

import com.pfplaybackend.api.playlist.application.dto.PlaylistTrackDto;
import com.pfplaybackend.api.playlist.application.service.TrackQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrackQueryController.class)
@Import(TrackQueryControllerTest.TestMethodSecurityConfig.class)
class TrackQueryControllerTest {

    @EnableMethodSecurity
    static class TestMethodSecurityConfig {}

    @Autowired MockMvc mockMvc;
    @MockBean TrackQueryService trackQueryService;
    @MockBean JwtDecoder jwtDecoder;

    @Test
    @DisplayName("getAllTracks — ROLE_MEMBER이면 200 OK")
    void getAllTracksMemberReturns200() throws Exception {
        // given
        Page<PlaylistTrackDto> page = new PageImpl<>(List.of(
                new PlaylistTrackDto(1L, "abc123", "Test Track", 1, "03:30", "https://example.com/thumb.jpg")
        ));
        when(trackQueryService.getTracks(1L, 0, 10)).thenReturn(page);

        // when & then
        mockMvc.perform(get("/api/v1/playlists/1/tracks")
                        .param("pageNumber", "0")
                        .param("pageSize", "10")
                        .with(jwt().authorities(() -> "ROLE_MEMBER"))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("getAllTracks — 인증 없으면 401")
    void getAllTracksUnauthenticatedReturns401() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/playlists/1/tracks")
                        .param("pageNumber", "0")
                        .param("pageSize", "10")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}
