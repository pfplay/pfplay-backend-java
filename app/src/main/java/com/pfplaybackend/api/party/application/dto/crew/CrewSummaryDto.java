package com.pfplaybackend.api.party.application.dto.crew;

import com.pfplaybackend.api.common.domain.enums.AvatarCompositionType;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;

public record CrewSummaryDto(
        long crewId,
        GradeType gradeType,
        String nickname,
        AvatarCompositionType avatarCompositionType,
        String avatarBodyUri,
        String avatarFaceUri,
        String avatarIconUri,
        int combinePositionX,
        int combinePositionY,
        double offsetX,
        double offsetY,
        double scale
) {
    public static CrewSummaryDto exitOnly(long crewId) {
        return new CrewSummaryDto(crewId, null, null, null, null, null, null, 0, 0, 0, 0, 0);
    }

    public static CrewSummaryDto from(CrewData crew, ProfileSettingDto p) {
        return new CrewSummaryDto(crew.getId(), crew.getGradeType(),
                p.nickname(), p.avatarCompositionType(), p.avatarBodyUri(), p.avatarFaceUri(), p.avatarIconUri(),
                p.combinePositionX(), p.combinePositionY(), p.offsetX(), p.offsetY(), p.scale());
    }
}
