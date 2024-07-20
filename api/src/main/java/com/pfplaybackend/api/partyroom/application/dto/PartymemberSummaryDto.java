package com.pfplaybackend.api.partyroom.application.dto;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PartymemberSummaryDto {
    // User
    private UserId userId;
    private AuthorityTier authorityTier;
    private String nickname;
    // Partymember
    private long memberId;
    private GradeType gradeType;
    // Avatar
    private String avatarBodyUri;
    private String avatarFaceUri;
    private String avatarIconUri;
}
