package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.partyroom.domain.specification.PartymemberGradeSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartyroomRegulationService {

    private final PartyroomAccessService partyroomAccessService;

    public void updateTargetPartymemberGrade() {
        // PartyInfo partyInfo = PartyContext.getPartyInfo();
        // partyInfo
        // 1. 호출자의 아이디와 파티 Grade
        // 2. 상대방의 아이디와 파티 Grade
        PartymemberGradeSpecification partymemberGradeSpecification = new PartymemberGradeSpecification();
        if(partymemberGradeSpecification.isAllowedToUpdateLevel()) {
            //
        }
    }

    public void forceOut() {
        partyroomAccessService.forceOut();
    }
}
