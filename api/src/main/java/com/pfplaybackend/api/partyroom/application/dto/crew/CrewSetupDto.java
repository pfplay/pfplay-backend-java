package com.pfplaybackend.api.partyroom.application.dto.crew;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Crew;
import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CrewSetupDto {
    private long crewId;
    private GradeType gradeType;
    private String nickname;
    private String avatarBodyUri;
    private String avatarFaceUri;
    private String avatarIconUri;
    private int combinePositionX;
    private int combinePositionY;

    public static CrewSetupDto from(Crew crew, ProfileSettingDto profileSettingDto) {
        return new CrewSetupDto(
                crew.getId(),
                crew.getGradeType(),
                profileSettingDto.getNickname(),
                profileSettingDto.getAvatarBodyUri(),
                profileSettingDto.getAvatarFaceUri(),
                profileSettingDto.getAvatarIconUri(),
                profileSettingDto.getCombinePositionX(),
                profileSettingDto.getCombinePositionY()
        );
    }
}
