package com.pfplaybackend.api.partyroom.domain.specification;

import com.pfplaybackend.api.partyroom.domain.model.enums.PartymemberGrade;

public class PartymemberGradeSpecification {
    public boolean isAllowedToUpdateLevel() {
        // PartyInfo partyInfo = PartyContext.getPartyInfo();
        // partyInfo
        // 1. 호출자의 아이디와 파티 Grade
        PartymemberGrade myGrade = PartymemberGrade.HOST;
        // 2. 상대방의 아이디와 파티 Grade
        PartymemberGrade targetGrade = PartymemberGrade.MODERATOR;
        if(myGrade.ordinal() > targetGrade.ordinal()) {
            return true;
        }else {
            return false;
        }
    }
    public boolean isAllowedToImpose() {
        return false;
    }
}
