package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.party.application.service.CrewBlockQueryService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CrewBlockQueryController.class)
class CrewBlockQueryControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean CrewBlockQueryService crewBlockQueryService;
    @MockBean JwtDecoder jwtDecoder;

    @Test
    @DisplayName("getBlockCrews — 200 OK")
    void getBlockCrews_returns200() throws Exception {
        // given
        when(crewBlockQueryService.getBlocks()).thenReturn(List.of());

        // when & then
        mockMvc.perform(get("/api/v1/crews/me/blocks")
                        .with(jwt().authorities(() -> "ROLE_MEMBER")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("getBlockCrews — 인증 없으면 401")
    void getBlockCrews_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/crews/me/blocks"))
                .andExpect(status().isUnauthorized());
    }
}
