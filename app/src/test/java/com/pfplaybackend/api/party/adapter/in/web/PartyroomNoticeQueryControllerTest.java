package com.pfplaybackend.api.party.adapter.in.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PartyroomNoticeQueryControllerTest extends AbstractPartyQueryWebMvcTest {

    @Test
    @DisplayName("getNotice — 200 OK")
    void getNoticeReturns200() throws Exception {
        // given
        when(partyroomNoticeQueryService.getNotice(any())).thenReturn("Welcome!");

        // when & then
        mockMvc.perform(get("/api/v1/partyrooms/1/notice")
                        .with(jwt().authorities(() -> "ROLE_MEMBER")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("getNotice — 인증 없으면 401")
    void getNoticeUnauthenticatedReturns401() throws Exception {
        mockMvc.perform(get("/api/v1/partyrooms/1/notice"))
                .andExpect(status().isUnauthorized());
    }
}
