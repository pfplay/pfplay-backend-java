package com.pfplaybackend.api.partyroom.application.service.impl;

import com.pfplaybackend.api.partyroom.application.service.PartymemberRegulationService;
import com.pfplaybackend.api.partyroom.domain.specification.PartymemberGradeSpecification;
import org.springframework.stereotype.Service;

@Service
public class PartymemberRegulationServiceImpl implements PartymemberRegulationService {

    void updateTargetPartymemberGrade() {
        // PartyInfo partyInfo = PartyContext.getPartyInfo();
        // partyInfo
        // 1. 호출자의 아이디와 파티 Grade
        // 2. 상대방의 아이디와 파티 Grade
        PartymemberGradeSpecification partymemberGradeSpecification = new PartymemberGradeSpecification();
        if(partymemberGradeSpecification.isAllowedToUpdateLevel()) {
            //

        }

    }
}
