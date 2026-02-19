package com.pfplaybackend.api.party.interfaces.api.rest.payload.response.access;


import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
@AllArgsConstructor
public class EnterPartyroomResponse {
    private long crewId;
    private GradeType gradeType;

    public static EnterPartyroomResponse from(CrewData crew) {
        return new EnterPartyroomResponse(crew.getId(), crew.getGradeType());
    }
}