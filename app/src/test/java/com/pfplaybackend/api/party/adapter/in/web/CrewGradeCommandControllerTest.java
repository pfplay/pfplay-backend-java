package com.pfplaybackend.api.party.adapter.in.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CrewGradeCommandControllerTest extends AbstractPartyCommandWebMvcTest {

    @Test
    @DisplayName("updateCrewGrade — 204 No Content")
    void updateCrewGradeReturns204() throws Exception {
        // given
        String body = """
                {"gradeType": "MODERATOR"}
                """;

        // when & then
        mockMvc.perform(patch("/api/v1/partyrooms/1/crews/1/grade")
                        .with(jwt().authorities(() -> "ROLE_MEMBER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent());

        verify(crewGradeCommandService).updateGrade(any(), any(), any());
    }

    @Test
    @DisplayName("updateCrewGrade — 인증 없으면 401")
    void updateCrewGradeUnauthenticatedReturns401() throws Exception {
        String body = """
                {"gradeType": "MODERATOR"}
                """;

        mockMvc.perform(patch("/api/v1/partyrooms/1/crews/1/grade")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }
}
