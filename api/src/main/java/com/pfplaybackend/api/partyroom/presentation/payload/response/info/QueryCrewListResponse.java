package com.pfplaybackend.api.partyroom.presentation.payload.response.info;

import com.pfplaybackend.api.partyroom.application.dto.crew.CrewSetupDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class QueryCrewListResponse {
    private List<CrewSetupDto> crewSetupDtoList;
    public static QueryCrewListResponse from(List<CrewSetupDto> crewSetupDtoList) {
        return new QueryCrewListResponse(crewSetupDtoList);
    }
}
