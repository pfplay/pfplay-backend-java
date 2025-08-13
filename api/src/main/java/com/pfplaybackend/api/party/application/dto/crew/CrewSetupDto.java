package com.pfplaybackend.api.party.application.dto.crew;

import com.pfplaybackend.api.party.domain.entity.domainmodel.Crew;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.profile.domain.enums.AvatarCompositionType;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CrewSetupDto {
    private long crewId;
    private GradeType gradeType;
    private String nickname;
    private AvatarCompositionType avatarCompositionType;
    private String avatarBodyUri;
    private String avatarFaceUri;
    private String avatarIconUri;
    private int combinePositionX;
    private int combinePositionY;
    private double offsetX;
    private double offsetY;
    private double scale;

    public static CrewSetupDto from(Crew crew, ProfileSettingDto profileSettingDto) {
        return new CrewSetupDto(
                crew.getId(),
                crew.getGradeType(),
                profileSettingDto.getNickname(),
                profileSettingDto.getAvatarCompositionType(),
                profileSettingDto.getAvatarBodyUri(),
                profileSettingDto.getAvatarFaceUri(),
                profileSettingDto.getAvatarIconUri(),
                profileSettingDto.getCombinePositionX(),
                profileSettingDto.getCombinePositionY(),
                profileSettingDto.getOffsetX(),
                profileSettingDto.getOffsetY(),
                profileSettingDto.getScale()
        );
    }
}
