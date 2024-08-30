package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.partyroom.domain.service.PartyroomDomainService;
import com.pfplaybackend.api.partyroom.domain.value.PartymemberId;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.presentation.payload.request.UpdateMemberGradeRequest;
import com.pfplaybackend.api.partyroom.presentation.payload.request.UpdateMemberPenaltyRequest;
import com.pfplaybackend.api.partyroom.repository.history.UserPenaltyHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartyroomRegulationService {

    private final UserPenaltyHistoryRepository userPenaltyHistoryRepository;
    private final PartyroomDomainService partyroomDomainService;
    private final PartyroomAccessService partyroomAccessService;

    public void updateGrade(PartyroomId partyroomId, PartymemberId partymemberId, UpdateMemberGradeRequest request) {
        // TODO
        // Exception 1. 타겟 멤버의 Grade 보다 높은가?
        // Exception 2. 호츨 멤버의 Grade 를 초과했는가?
    }

    public void updatePenalty(PartyroomId partyroomId, PartymemberId partymemberId, UpdateMemberPenaltyRequest request) {
        // TODO
    }
}
