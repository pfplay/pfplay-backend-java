package com.pfplaybackend.api.party.application.dto.result;

import com.pfplaybackend.api.party.domain.entity.data.history.CrewPenaltyHistoryData;
import com.pfplaybackend.api.party.domain.enums.PenaltyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PenaltyResult {
    Long penaltyId;
    PenaltyType penaltyType;
    Long crewId;

    public static PenaltyResult from(CrewPenaltyHistoryData crewPenaltyHistoryData) {
        return PenaltyResult.builder()
                .penaltyId(crewPenaltyHistoryData.getId())
                .crewId(crewPenaltyHistoryData.getPunishedCrewId().getId())
                .penaltyType(crewPenaltyHistoryData.getPenaltyType())
                .build();
    }
}
