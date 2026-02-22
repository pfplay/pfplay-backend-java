package com.pfplaybackend.api.party.adapter.in.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PartyroomNoticeCommandController.class)
class PartyroomNoticeCommandControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean JwtDecoder jwtDecoder;

    @Test
    @DisplayName("registerNotice — 200 OK")
    void registerNotice_returns200() throws Exception {
        mockMvc.perform(put("/api/v1/partyrooms/1/notice")
                        .with(jwt().authorities(() -> "ROLE_MEMBER"))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("registerNotice — 인증 없으면 401")
    void registerNotice_unauthenticated_returns401() throws Exception {
        mockMvc.perform(put("/api/v1/partyrooms/1/notice")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}
