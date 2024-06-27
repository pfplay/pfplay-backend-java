package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.partyroom.domain.service.PartyroomDomainService;
import com.pfplaybackend.api.partyroom.domain.value.PartymemberId;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.presentation.payload.request.UpdateMemberGradeRequest;
import com.pfplaybackend.api.partyroom.presentation.payload.request.UpdateMemberPenaltyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartyroomRegulationService {

    private final PartyroomDomainService partyroomDomainService;
    private final PartyroomAccessService partyroomAccessService;

    public void updateGrade(PartyroomId partyroomId, PartymemberId partymemberId, UpdateMemberGradeRequest request) {

    }

    public void updatePenalty(PartyroomId partyroomId, PartymemberId partymemberId, UpdateMemberPenaltyRequest request) {
        // TODO
    }
}
