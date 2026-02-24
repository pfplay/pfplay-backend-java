package com.pfplaybackend.api.party.application.dto.crew;

import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.party.domain.enums.GradeType;

public record CrewDto(Long crewId, UserId userId, GradeType gradeType) {}
