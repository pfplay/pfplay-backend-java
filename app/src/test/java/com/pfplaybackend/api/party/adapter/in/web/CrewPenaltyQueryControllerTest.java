package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.party.application.service.CrewPenaltyQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CrewPenaltyQueryController.class)
class CrewPenaltyQueryControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean CrewPenaltyQueryService crewPenaltyQueryService;
    @MockBean JwtDecoder jwtDecoder;

    @Test
    @DisplayName("getAllPenalties — 200 OK")
    void getAllPenalties_returns200() throws Exception {
        // given
        when(crewPenaltyQueryService.getPenalties(any())).thenReturn(List.of());

        // when & then
        mockMvc.perform(get("/api/v1/partyrooms/1/penalties")
                        .with(jwt().authorities(() -> "ROLE_MEMBER")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("getAllPenalties — 인증 없으면 401")
    void getAllPenalties_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/partyrooms/1/penalties"))
                .andExpect(status().isUnauthorized());
    }
}
