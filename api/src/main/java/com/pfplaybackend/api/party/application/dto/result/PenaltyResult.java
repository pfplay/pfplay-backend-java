package com.pfplaybackend.api.party.application.dto.result;

import com.pfplaybackend.api.party.domain.entity.data.history.CrewPenaltyHistoryData;
import com.pfplaybackend.api.party.domain.enums.PenaltyType;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;

public record PenaltyResult(
        Long penaltyId,
        PenaltyType penaltyType,
        Long crewId,
        String nickname,
        String avatarIconUri
) {
    public static PenaltyResult from(CrewPenaltyHistoryData crewPenaltyHistoryData, ProfileSettingDto profileSettingDto) {
        return new PenaltyResult(
                crewPenaltyHistoryData.getId(),
                crewPenaltyHistoryData.getPenaltyType(),
                crewPenaltyHistoryData.getPunishedCrewId().getId(),
                profileSettingDto.nickname(),
                profileSettingDto.avatarIconUri()
        );
    }
}
