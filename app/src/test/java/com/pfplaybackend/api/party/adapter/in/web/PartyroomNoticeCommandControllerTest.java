package com.pfplaybackend.api.party.adapter.in.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PartyroomNoticeCommandControllerTest extends AbstractPartyCommandWebMvcTest {

    @Test
    @DisplayName("registerNotice — 204 No Content")
    void registerNoticeReturns204() throws Exception {
        mockMvc.perform(put("/api/v1/partyrooms/1/notice")
                        .with(jwt().authorities(() -> "ROLE_MEMBER"))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("registerNotice — 인증 없으면 401")
    void registerNoticeUnauthenticatedReturns401() throws Exception {
        mockMvc.perform(put("/api/v1/partyrooms/1/notice")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}
