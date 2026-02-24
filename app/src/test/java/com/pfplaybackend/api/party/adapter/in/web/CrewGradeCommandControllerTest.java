package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.party.application.service.CrewGradeCommandService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CrewGradeCommandController.class)
class CrewGradeCommandControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean CrewGradeCommandService crewGradeCommandService;
    @MockBean JwtDecoder jwtDecoder;

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
