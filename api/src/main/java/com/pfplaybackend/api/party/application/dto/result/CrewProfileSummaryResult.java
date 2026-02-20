package com.pfplaybackend.api.party.application.dto.result;

import com.pfplaybackend.api.user.application.dto.shared.ActivitySummaryDto;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSummaryDto;

import java.util.List;

public record CrewProfileSummaryResult(
        Long crewId,
        String nickname,
        String introduction,
        String avatarBodyUri,
        int combinePositionX,
        int combinePositionY,
        String avatarFaceUri,
        List<ActivitySummaryDto> activitySummaries
) {
    public static CrewProfileSummaryResult from(Long crewId, ProfileSummaryDto dto) {
        return new CrewProfileSummaryResult(
                crewId,
                dto.nickname(),
                dto.introduction(),
                dto.avatarBodyUri(),
                dto.combinePositionX(),
                dto.combinePositionY(),
                dto.avatarFaceUri(),
                dto.activitySummaries()
        );
    }
}
