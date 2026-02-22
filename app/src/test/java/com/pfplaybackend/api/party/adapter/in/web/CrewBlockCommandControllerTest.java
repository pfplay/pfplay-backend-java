package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.party.application.service.CrewBlockCommandService;
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

@WebMvcTest(CrewBlockCommandController.class)
class CrewBlockCommandControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean CrewBlockCommandService crewBlockCommandService;
    @MockBean JwtDecoder jwtDecoder;

    @Test
    @DisplayName("blockOtherCrew — 200 OK")
    void blockOtherCrew_returns200() throws Exception {
        String body = """
                {
                    "crewId": 10
                }
                """;

        mockMvc.perform(post("/api/v1/crews/me/blocks")
                        .with(jwt().authorities(() -> "ROLE_MEMBER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("unblockOther — 200 OK")
    void unblockOther_returns200() throws Exception {
        mockMvc.perform(delete("/api/v1/crews/me/blocks/100")
                        .with(jwt().authorities(() -> "ROLE_MEMBER"))
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}
