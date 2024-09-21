package com.pfplaybackend.api.partyroom.presentation.payload.response;

import com.pfplaybackend.api.partyroom.application.dto.DisplayDto;
import com.pfplaybackend.api.partyroom.application.dto.CrewSetupDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class QueryPartyroomSetupResponse {
    List<CrewSetupDto> crews;
    DisplayDto display;

    public static QueryPartyroomSetupResponse from(List<CrewSetupDto> crews, DisplayDto display) {
        return new QueryPartyroomSetupResponse(crews, display);
    }
}