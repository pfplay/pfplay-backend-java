package com.pfplaybackend.api.partyroom.domain.specification;

import com.pfplaybackend.api.partyroom.domain.model.collection.Partymember;
import com.pfplaybackend.api.partyroom.domain.model.enums.MemberGrade;
import com.pfplaybackend.api.user.domain.model.domain.Member;

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
