package com.pfplaybackend.api.partyroom.presentation.payload.request.regulation;

import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import lombok.Getter;

@Getter
public class AdjustGradeRequest {
    private GradeType gradeType;
}
