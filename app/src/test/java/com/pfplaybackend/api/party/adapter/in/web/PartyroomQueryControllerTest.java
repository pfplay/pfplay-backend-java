package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.party.application.dto.result.DjQueueInfoResult;
import com.pfplaybackend.api.party.domain.enums.QueueStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PartyroomQueryControllerTest extends AbstractPartyQueryWebMvcTest {

    @Test
    @DisplayName("getPartyrooms — 200 OK + 파티룸 목록 반환")
    void getPartyroomsReturns200() throws Exception {
        // given
        when(partyroomQueryService.getAllPartyrooms()).thenReturn(List.of());
        when(partyroomQueryService.getPrimariesAvatarSettings(any())).thenReturn(Map.of());

        // when & then
        mockMvc.perform(get("/api/v1/partyrooms")
                        .with(jwt().authorities(() -> "ROLE_MEMBER")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("getPartyroomSummaryInfo — 200 OK")
    void getPartyroomSummaryInfoReturns200() throws Exception {
        // given
        when(partyroomQueryService.getSummaryInfo(any())).thenReturn(null);

        // when & then
        mockMvc.perform(get("/api/v1/partyrooms/1/summary")
                        .with(jwt().authorities(() -> "ROLE_MEMBER")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("getDjQueueInfo — 200 OK")
    void getDjQueueInfoReturns200() throws Exception {
        // given
        when(partyroomQueryService.getDjQueueInfo(any())).thenReturn(
                new DjQueueInfoResult(false, QueueStatus.OPEN, false, null, List.of()));

        // when & then
        mockMvc.perform(get("/api/v1/partyrooms/1/dj-queue")
                        .with(jwt().authorities(() -> "ROLE_MEMBER")))
                .andExpect(status().isOk());
    }
}
