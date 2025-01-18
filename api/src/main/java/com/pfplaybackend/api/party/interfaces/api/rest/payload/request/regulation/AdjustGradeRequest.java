package com.pfplaybackend.api.party.interfaces.api.rest.payload.request.regulation;

import com.pfplaybackend.api.party.domain.enums.GradeType;
import lombok.Getter;

@Getter
public class AdjustGradeRequest {
    private GradeType gradeType;
}
