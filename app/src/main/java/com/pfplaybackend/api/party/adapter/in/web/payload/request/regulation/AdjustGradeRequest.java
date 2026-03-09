package com.pfplaybackend.api.party.adapter.in.web.payload.request.regulation;

import com.pfplaybackend.api.party.domain.enums.GradeType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class AdjustGradeRequest {
    @NotNull(message = "gradeType is required.")
    private GradeType gradeType;
}
