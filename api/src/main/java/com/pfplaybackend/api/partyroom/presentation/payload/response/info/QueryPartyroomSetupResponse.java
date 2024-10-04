package com.pfplaybackend.api.partyroom.presentation.payload.response.info;

import com.pfplaybackend.api.partyroom.application.dto.playback.DisplayDto;
import com.pfplaybackend.api.partyroom.application.dto.crew.CrewSetupDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class QueryPartyroomSetupResponse {
    private List<CrewSetupDto> crews;
    private DisplayDto display;

    public static QueryPartyroomSetupResponse from(List<CrewSetupDto> crews, DisplayDto display) {
        return new QueryPartyroomSetupResponse(crews, display);
    }
}