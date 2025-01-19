package com.pfplaybackend.api.party.application.dto.result;

import com.pfplaybackend.api.party.domain.entity.data.history.CrewPenaltyHistoryData;
import com.pfplaybackend.api.party.domain.enums.PenaltyType;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PenaltyResult {
    private Long penaltyId;
    private PenaltyType penaltyType;
    private Long crewId;
    private String nickname;
    private String avatarIconUri;

    public static PenaltyResult from(CrewPenaltyHistoryData crewPenaltyHistoryData, ProfileSettingDto profileSettingDto) {
        return PenaltyResult.builder()
                .penaltyId(crewPenaltyHistoryData.getId())
                .crewId(crewPenaltyHistoryData.getPunishedCrewId().getId())
                .penaltyType(crewPenaltyHistoryData.getPenaltyType())
                .nickname(profileSettingDto.getNickname())
                .avatarIconUri(profileSettingDto.getAvatarIconUri())
                .build();
    }
}
