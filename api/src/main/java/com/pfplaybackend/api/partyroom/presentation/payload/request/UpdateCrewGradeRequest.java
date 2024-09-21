package com.pfplaybackend.api.partyroom.presentation.payload.request;

import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import lombok.Getter;

@Getter
public class UpdateCrewGradeRequest {
    GradeType gradeType;
}
