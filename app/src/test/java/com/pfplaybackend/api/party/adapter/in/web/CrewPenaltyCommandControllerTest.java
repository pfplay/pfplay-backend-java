package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.party.application.service.CrewPenaltyCommandService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CrewPenaltyCommandController.class)
class CrewPenaltyCommandControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean CrewPenaltyCommandService crewPenaltyCommandService;
    @MockBean JwtDecoder jwtDecoder;

    @Test
    @DisplayName("imposeCrewPenalty — 202 Accepted")
    void imposeCrewPenalty_returns202() throws Exception {
        // given
        String body = """
                {"crewId": 1, "penaltyType": "ONE_TIME_EXPULSION", "detail": "Disruptive behavior"}
                """;

        // when & then
        mockMvc.perform(post("/api/v1/partyrooms/1/penalties")
                        .with(jwt().authorities(() -> "ROLE_MEMBER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isAccepted());
    }

    @Test
    @DisplayName("releaseCrewPenalty — 202 Accepted")
    void releaseCrewPenalty_returns202() throws Exception {
        mockMvc.perform(delete("/api/v1/partyrooms/1/penalties/100")
                        .with(jwt().authorities(() -> "ROLE_MEMBER"))
                        .with(csrf()))
                .andExpect(status().isAccepted());
    }

    @Test
    @DisplayName("imposeCrewPenalty — 인증 없으면 401")
    void imposeCrewPenalty_unauthenticated_returns401() throws Exception {
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
