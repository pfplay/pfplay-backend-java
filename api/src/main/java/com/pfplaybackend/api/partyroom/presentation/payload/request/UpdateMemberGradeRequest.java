package com.pfplaybackend.api.partyroom.presentation.payload.request;

import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
public class UpdateMemberGradeRequest {
    GradeType gradeType;
}
