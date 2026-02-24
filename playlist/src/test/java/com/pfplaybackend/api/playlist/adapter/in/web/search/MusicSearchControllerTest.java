package com.pfplaybackend.api.playlist.adapter.in.web.search;

import com.pfplaybackend.api.playlist.application.dto.search.SearchResultDto;
import com.pfplaybackend.api.playlist.application.service.search.MusicSearchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MusicSearchController.class)
@Import(MusicSearchControllerTest.TestMethodSecurityConfig.class)
class MusicSearchControllerTest {

    @EnableMethodSecurity
    static class TestMethodSecurityConfig {}

    @Autowired MockMvc mockMvc;
    @MockBean MusicSearchService musicSearchService;
    @MockBean JwtDecoder jwtDecoder;

    @Test
    @DisplayName("getSearchList — ROLE_MEMBER이면 200 OK")
    void getSearchListMemberReturns200() throws Exception {
        // given
        SearchResultDto searchResult = new SearchResultDto("success", List.of());
        when(musicSearchService.getSearchList("test")).thenReturn(searchResult);

        // when & then
        mockMvc.perform(get("/api/v1/music-search")
                        .param("q", "test")
                        .with(jwt().authorities(() -> "ROLE_MEMBER"))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("getSearchList — 인증 없으면 401")
    void getSearchListUnauthenticatedReturns401() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/music-search")
                        .param("q", "test")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}
