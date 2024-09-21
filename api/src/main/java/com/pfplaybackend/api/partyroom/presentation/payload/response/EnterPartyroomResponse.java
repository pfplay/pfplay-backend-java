package com.pfplaybackend.api.partyroom.presentation.payload.response;


import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Crew;
import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
@AllArgsConstructor
public class EnterPartyroomResponse {
    private long crewId;
    private GradeType gradeType;

    public static EnterPartyroomResponse from(Crew crew) {
        return new EnterPartyroomResponse(crew.getId(), crew.getGradeType());
    }
}