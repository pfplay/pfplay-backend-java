package com.pfplaybackend.api.party.adapter.in.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CrewBlockQueryControllerTest extends AbstractPartyQueryWebMvcTest {

    @Test
    @DisplayName("getBlockCrews — 200 OK")
    void getBlockCrewsReturns200() throws Exception {
        // given
        when(crewBlockQueryService.getBlocks()).thenReturn(List.of());

        // when & then
        mockMvc.perform(get("/api/v1/crews/me/blocks")
                        .with(jwt().authorities(() -> "ROLE_MEMBER")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("getBlockCrews — 인증 없으면 401")
    void getBlockCrewsUnauthenticatedReturns401() throws Exception {
        mockMvc.perform(get("/api/v1/crews/me/blocks"))
                .andExpect(status().isUnauthorized());
    }
}
