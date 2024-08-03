package com.pfplaybackend.api.partyroom.presentation.payload.response;


import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partymember;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
@AllArgsConstructor
public class EnterPartyroomResponse {
    private long memberId;
    private GradeType gradeType;

    public static EnterPartyroomResponse from(Partymember partymember) {
        return new EnterPartyroomResponse(partymember.getId(), partymember.getGradeType());
    }
}