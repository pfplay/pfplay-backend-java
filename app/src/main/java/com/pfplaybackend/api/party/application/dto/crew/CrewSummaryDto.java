package com.pfplaybackend.api.party.application.dto.crew;

import com.pfplaybackend.api.party.application.dto.shared.AvatarProfile;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;

public record CrewSummaryDto(
        long crewId,
        GradeType gradeType,
        String nickname,
        AvatarProfile avatar
) {
    public static CrewSummaryDto from(CrewData crew, ProfileSettingDto p) {
        return new CrewSummaryDto(crew.getId(), crew.getGradeType(), p.nickname(),
                AvatarProfile.from(p.avatarCompositionType(), p.avatarBodyUri(), p.avatarFaceUri(),
                        p.avatarIconUri(), p.combinePositionX(), p.combinePositionY(),
                        p.offsetX(), p.offsetY(), p.scale()));
    }
}
