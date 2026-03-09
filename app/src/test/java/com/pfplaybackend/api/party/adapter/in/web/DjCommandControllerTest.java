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

class DjCommandControllerTest extends AbstractPartyCommandWebMvcTest {

    @Test
    @DisplayName("enqueueDj — 201 Created + djId 반환")
    void enqueueDjReturns201WithDjId() throws Exception {
        // given
        String body = """
                {
                    "playlistId": 1
                }
                """;
        when(djCommandService.enqueueDj(any(), any())).thenReturn(42L);

        // when & then
        mockMvc.perform(post("/api/v1/partyrooms/1/dj-queue")
                        .with(jwt().authorities(() -> "ROLE_MEMBER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.djId").value(42));
    }

    @Test
    @DisplayName("dequeueDj — 204 No Content")
    void dequeueDjReturns204() throws Exception {
        mockMvc.perform(delete("/api/v1/partyrooms/1/dj-queue/me")
                        .with(jwt().authorities(() -> "ROLE_MEMBER"))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("enqueueDj — 인증 없으면 401")
    void enqueueDjUnauthenticatedReturns401() throws Exception {
        String body = """
                {
                    "playlistId": 1
                }
                """;

        mockMvc.perform(post("/api/v1/partyrooms/1/dj-queue")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }
}
