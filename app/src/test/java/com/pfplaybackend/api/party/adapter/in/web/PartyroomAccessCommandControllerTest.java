package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.party.application.service.PartyroomAccessCommandService;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PartyroomAccessCommandController.class)
class PartyroomAccessCommandControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean PartyroomAccessCommandService partyroomAccessCommandService;
    @MockBean JwtDecoder jwtDecoder;

    @Test
    @DisplayName("enterPartyroom — 200 OK")
    void enterPartyroom_returns200() throws Exception {
        // given
        CrewData crew = mock(CrewData.class);
        when(crew.getId()).thenReturn(1L);
        when(crew.getGradeType()).thenReturn(GradeType.CLUBBER);
        when(partyroomAccessCommandService.tryEnter(any())).thenReturn(crew);

        // when & then
        mockMvc.perform(post("/api/v1/partyrooms/1/enter")
                        .with(jwt().authorities(() -> "ROLE_MEMBER"))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("exitPartyroom — 200 OK")
    void exitPartyroom_returns200() throws Exception {
        mockMvc.perform(post("/api/v1/partyrooms/1/exit")
                        .with(jwt().authorities(() -> "ROLE_MEMBER"))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("enterPartyroom — 인증 없으면 401")
    void enterPartyroom_unauthenticated_returns401() throws Exception {
        mockMvc.perform(post("/api/v1/partyrooms/1/enter")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}
