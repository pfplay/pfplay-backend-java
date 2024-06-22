package com.pfplaybackend.api.partyroom.domain.specification;

import com.pfplaybackend.api.partyroom.domain.enums.MemberGrade;

public class PartymemberGradeSpecification {
    public boolean isAllowedToUpdateLevel() {
        // PartyInfo partyInfo = PartyContext.getPartyInfo();
        // partyInfo
        // 1. 호출자의 아이디와 파티 Grade
        MemberGrade myGrade = MemberGrade.HOST;
        // 2. 상대방의 아이디와 파티 Grade
        MemberGrade targetGrade = MemberGrade.MODERATE;
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
