package com.pfplaybackend.api.partyview.application.dto;

import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.common.domain.enums.AvatarCompositionType;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;

public record CrewSetupDto(
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
    public static CrewSetupDto from(CrewData crew, ProfileSettingDto p) {
        return new CrewSetupDto(crew.getId(), crew.getGradeType(),
                p.nickname(), p.avatarCompositionType(), p.avatarBodyUri(), p.avatarFaceUri(), p.avatarIconUri(),
                p.combinePositionX(), p.combinePositionY(), p.offsetX(), p.offsetY(), p.scale());
    }
}
