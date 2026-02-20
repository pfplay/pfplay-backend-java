package com.pfplaybackend.api.partyview.adapter.in.web.payload.response;

import com.pfplaybackend.api.partyview.application.dto.DisplayDto;
import com.pfplaybackend.api.partyview.application.dto.CrewSetupDto;
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
