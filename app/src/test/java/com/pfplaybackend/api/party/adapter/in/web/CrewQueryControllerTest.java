package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.party.application.dto.result.CrewProfileSummaryResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CrewQueryControllerTest extends AbstractPartyQueryWebMvcTest {

    @Test
    @DisplayName("getOtherProfileSummary — ROLE_MEMBER이면 200 OK")
    void getOtherProfileSummaryMemberReturns200() throws Exception {
        // given
        CrewProfileSummaryResult result = new CrewProfileSummaryResult(
                1L, "nickname", "intro", "body-uri", 0, 0, "face-uri", List.of());
        when(partyroomQueryService.getProfileSummaryByCrewId(1L)).thenReturn(result);

        // when & then
        mockMvc.perform(get("/api/v1/crews/1/profile/summary")
                        .with(jwt().authorities(() -> "ROLE_MEMBER")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("getOtherProfileSummary — ROLE_GUEST이면 200 OK")
    void getOtherProfileSummaryGuestReturns200() throws Exception {
        // given
        CrewProfileSummaryResult result = new CrewProfileSummaryResult(
                1L, "nickname", "intro", "body-uri", 0, 0, "face-uri", List.of());
        when(partyroomQueryService.getProfileSummaryByCrewId(1L)).thenReturn(result);

        // when & then
        mockMvc.perform(get("/api/v1/crews/1/profile/summary")
                        .with(jwt().authorities(() -> "ROLE_GUEST")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("getOtherProfileSummary — 인증 없으면 401")
    void getOtherProfileSummaryUnauthenticatedReturns401() throws Exception {
        mockMvc.perform(get("/api/v1/crews/1/profile/summary"))
                .andExpect(status().isUnauthorized());
    }
}
