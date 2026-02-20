package com.pfplaybackend.api.party.adapter.in.web.payload.request.regulation;

import com.pfplaybackend.api.party.domain.enums.GradeType;
import lombok.Getter;

@Getter
public class AdjustGradeRequest {
    private GradeType gradeType;
}
