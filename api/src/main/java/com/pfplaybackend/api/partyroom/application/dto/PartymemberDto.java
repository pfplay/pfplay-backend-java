package com.pfplaybackend.api.partyroom.application.dto;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import com.pfplaybackend.api.user.domain.value.UserId;

public class PartymemberDto {
    // User
    private String uid;
    private AuthorityTier authorityTier;
    private String nickname;
    // Partymember
    private long memberId;
    private GradeType gradeType;
    // Avatar
    private String avatarBodyUri;
    private String avatarFaceUri;
}
