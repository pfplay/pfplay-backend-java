package com.pfplaybackend.api.partyroom.application.dto.crew;

import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Crew;
import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CrewSummaryDto {
    // Crew
    private long crewId;
    private GradeType gradeType;
    // Profile
    private String nickname;
    private String avatarBodyUri;
    private String avatarFaceUri;
    private String avatarIconUri;
    private int combinePositionX;
    private int combinePositionY;

    public CrewSummaryDto(long crewId) {
        this.crewId = crewId;
    }

    public static CrewSummaryDto from(Crew crew, ProfileSettingDto profileSettingDto) {
        return new CrewSummaryDto(
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
