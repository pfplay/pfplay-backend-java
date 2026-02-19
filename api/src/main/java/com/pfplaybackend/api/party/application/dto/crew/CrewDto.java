package com.pfplaybackend.api.party.application.dto.crew;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.common.domain.value.UserId;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CrewDto {
    Long crewId;
    UserId userId;
    AuthorityTier authorityTier;
    GradeType gradeType;
}
