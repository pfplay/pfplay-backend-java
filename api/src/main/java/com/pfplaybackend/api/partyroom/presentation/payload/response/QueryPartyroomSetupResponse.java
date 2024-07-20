package com.pfplaybackend.api.partyroom.presentation.payload.response;

import com.pfplaybackend.api.partyroom.application.dto.DisplayDto;
import com.pfplaybackend.api.partyroom.application.dto.PartymemberSetupDto;
import com.pfplaybackend.api.partyroom.application.dto.PartymemberSummaryDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class QueryPartyroomSetupResponse {
    List<PartymemberSetupDto> members;
    DisplayDto display;

    public static QueryPartyroomSetupResponse from(List<PartymemberSetupDto> members, DisplayDto display) {
        return new QueryPartyroomSetupResponse(members, display);
    }
}
