package com.pfplaybackend.api.party.adapter.in.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CrewBlockCommandControllerTest extends AbstractPartyCommandWebMvcTest {

    @Test
    @DisplayName("blockOtherCrew — 201 Created")
    void blockOtherCrewReturns201() throws Exception {
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
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("unblockOther — 204 No Content")
    void unblockOtherReturns204() throws Exception {
        mockMvc.perform(delete("/api/v1/crews/me/blocks/100")
                        .with(jwt().authorities(() -> "ROLE_MEMBER"))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
