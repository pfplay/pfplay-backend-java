package com.pfplaybackend.api.party.adapter.in.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CrewPenaltyCommandControllerTest extends AbstractPartyCommandWebMvcTest {

    @Test
    @DisplayName("imposeCrewPenalty — 201 Created + penaltyId 반환")
    void imposeCrewPenaltyReturns201WithPenaltyId() throws Exception {
        // given
        String body = """
                {"crewId": 1, "penaltyType": "ONE_TIME_EXPULSION", "detail": "Disruptive behavior"}
                """;
        when(crewPenaltyCommandService.addPenalty(any(), any())).thenReturn(77L);

        // when & then
        mockMvc.perform(post("/api/v1/partyrooms/1/penalties")
                        .with(jwt().authorities(() -> "ROLE_MEMBER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.penaltyId").value(77));
    }

    @Test
    @DisplayName("releaseCrewPenalty — 204 No Content")
    void releaseCrewPenaltyReturns204() throws Exception {
        mockMvc.perform(delete("/api/v1/partyrooms/1/penalties/100")
                        .with(jwt().authorities(() -> "ROLE_MEMBER"))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("imposeCrewPenalty — 인증 없으면 401")
    void imposeCrewPenaltyUnauthenticatedReturns401() throws Exception {
        String body = """
                {"crewId": 1, "penaltyType": "ONE_TIME_EXPULSION", "detail": "Disruptive behavior"}
                """;

        mockMvc.perform(post("/api/v1/partyrooms/1/penalties")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }
}
