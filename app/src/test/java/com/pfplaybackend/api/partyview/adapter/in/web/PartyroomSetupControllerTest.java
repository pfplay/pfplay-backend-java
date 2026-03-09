package com.pfplaybackend.api.partyview.adapter.in.web;

import com.pfplaybackend.api.party.adapter.in.web.AbstractPartyQueryWebMvcTest;
import com.pfplaybackend.api.partyview.application.dto.DisplayDto;
import com.pfplaybackend.api.partyview.application.dto.result.PartyroomSetupResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PartyroomSetupControllerTest extends AbstractPartyQueryWebMvcTest {

    @Test
    @DisplayName("getSetupInfo — 200 OK")
    void getSetupInfoReturns200() throws Exception {
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
    void getSetupInfoUnauthenticatedReturns401() throws Exception {
        mockMvc.perform(get("/api/v1/partyrooms/1/setup"))
                .andExpect(status().isUnauthorized());
    }
}
