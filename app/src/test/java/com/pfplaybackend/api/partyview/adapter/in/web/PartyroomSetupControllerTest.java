package com.pfplaybackend.api.partyview.adapter.in.web;

import com.pfplaybackend.api.partyview.application.dto.CrewSetupDto;
import com.pfplaybackend.api.partyview.application.dto.DisplayDto;
import com.pfplaybackend.api.partyview.application.dto.result.PartyroomSetupResult;
import com.pfplaybackend.api.partyview.application.service.PartyroomSetupQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PartyroomSetupController.class)
class PartyroomSetupControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean PartyroomSetupQueryService partyroomSetupQueryService;
    @MockBean JwtDecoder jwtDecoder;

    @Test
    @DisplayName("getSetupInfo — 200 OK")
    void getSetupInfo_returns200() throws Exception {
        // given
        PartyroomSetupResult result = new PartyroomSetupResult(List.of(), mock(DisplayDto.class));
        when(partyroomSetupQueryService.getSetupInfo(any())).thenReturn(result);

        // when & then
        mockMvc.perform(get("/api/v1/partyrooms/1/setup")
                        .with(jwt().authorities(() -> "ROLE_MEMBER")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("getSetupInfo — 인증 없으면 401")
    void getSetupInfo_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/partyrooms/1/setup"))
                .andExpect(status().isUnauthorized());
    }
}
