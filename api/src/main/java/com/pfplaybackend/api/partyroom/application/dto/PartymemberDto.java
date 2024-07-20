package com.pfplaybackend.api.partyroom.application.dto;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PartymemberDto {
    Long memberId;
    UserId userId;
    AuthorityTier authorityTier;
    GradeType gradeType;
}
