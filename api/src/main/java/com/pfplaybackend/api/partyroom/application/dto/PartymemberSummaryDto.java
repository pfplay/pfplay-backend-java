package com.pfplaybackend.api.partyroom.application.dto;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partymember;
import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartymemberSummaryDto {
    // User
    //    private UserId userId;
    //    private AuthorityTier authorityTier;
    // Partymember
    private long memberId;
    private GradeType gradeType;
    // Profile
    private String nickname;
    private String avatarBodyUri;
    private String avatarFaceUri;
    private String avatarIconUri;
    private int combinePositionX;
    private int combinePositionY;

    public PartymemberSummaryDto(long partymemberId) {
        this.memberId = partymemberId;
    }

    public static PartymemberSummaryDto from(Partymember partymember, ProfileSettingDto profileSettingDto) {
        return new PartymemberSummaryDto(
                partymember.getId(),
                partymember.getGradeType(),
                profileSettingDto.getNickname(),
                profileSettingDto.getAvatarBodyUri(),
                profileSettingDto.getAvatarFaceUri(),
                profileSettingDto.getAvatarIconUri(),
                profileSettingDto.getCombinePositionX(),
                profileSettingDto.getCombinePositionY()
        );
    }
}
