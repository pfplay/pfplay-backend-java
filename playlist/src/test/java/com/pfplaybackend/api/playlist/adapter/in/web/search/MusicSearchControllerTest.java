package com.pfplaybackend.api.playlist.adapter.in.web.search;

import com.pfplaybackend.api.playlist.adapter.in.web.AbstractPlaylistWebMvcTest;
import com.pfplaybackend.api.playlist.application.dto.search.SearchResultDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MusicSearchControllerTest extends AbstractPlaylistWebMvcTest {

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
