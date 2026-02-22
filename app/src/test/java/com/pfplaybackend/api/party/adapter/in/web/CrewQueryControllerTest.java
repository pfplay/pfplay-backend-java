package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.party.application.dto.result.CrewProfileSummaryResult;
import com.pfplaybackend.api.party.application.service.PartyroomQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CrewQueryController.class)
@Import(CrewQueryControllerTest.TestMethodSecurityConfig.class)
class CrewQueryControllerTest {

    @EnableMethodSecurity
    static class TestMethodSecurityConfig {}

    @Autowired MockMvc mockMvc;
    @MockBean PartyroomQueryService partyroomQueryService;
    @MockBean JwtDecoder jwtDecoder;

    @Test
    @DisplayName("getOtherProfileSummary — ROLE_MEMBER이면 200 OK")
    void getOtherProfileSummary_member_returns200() throws Exception {
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
    void getOtherProfileSummary_guest_returns200() throws Exception {
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
    void getOtherProfileSummary_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/crews/1/profile/summary"))
                .andExpect(status().isUnauthorized());
    }
}
