package com.pfplaybackend.api.party.application.dto.command;

import com.pfplaybackend.api.party.domain.enums.GradeType;

public record AdjustGradeCommand(GradeType gradeType) {}
