package com.pfplaybackend.api.party.adapter.in.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CrewPenaltyQueryControllerTest extends AbstractPartyQueryWebMvcTest {

    @Test
    @DisplayName("getAllPenalties — 200 OK")
    void getAllPenaltiesReturns200() throws Exception {
        // given
        when(crewPenaltyQueryService.getPenalties(any())).thenReturn(List.of());

        // when & then
        mockMvc.perform(get("/api/v1/partyrooms/1/penalties")
                        .with(jwt().authorities(() -> "ROLE_MEMBER")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("getAllPenalties — 인증 없으면 401")
    void getAllPenaltiesUnauthenticatedReturns401() throws Exception {
        mockMvc.perform(get("/api/v1/partyrooms/1/penalties"))
                .andExpect(status().isUnauthorized());
    }
}
