package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.partyroom.domain.entity.converter.PartyroomConverter;
import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.partyroom.domain.service.PartyroomDomainService;
import com.pfplaybackend.api.partyroom.domain.value.PartymemberId;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.exception.PartyroomException;
import com.pfplaybackend.api.partyroom.presentation.payload.request.UpdateMemberGradeRequest;
import com.pfplaybackend.api.partyroom.presentation.payload.request.UpdateMemberPenaltyRequest;
import com.pfplaybackend.api.partyroom.repository.PartyroomRepository;
import com.pfplaybackend.api.partyroom.repository.history.UserPenaltyHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartyroomRegulationService {

    private final PartyroomRepository partyroomRepository;
    private final PartyroomConverter partyroomConverter;
    private final UserPenaltyHistoryRepository userPenaltyHistoryRepository;
    private final PartyroomDomainService partyroomDomainService;
    private final PartyroomAccessService partyroomAccessService;


    @Transactional
    public void updateGrade(PartyroomId partyroomId, PartymemberId partymemberId, UpdateMemberGradeRequest request) {
        // TODO
        // Exception 0. HOST 등급으로 조정 불가능
        // Exception 1. 호출 멤버와 타겟 멤버는 같은 파티룸에 위치하는가?
        // Exception 2. 타겟 멤버의 Grade 보다 높은가?
        // Exception 3. 호츨 멤버의 Grade 를 초과했는가?
        PartyroomData partyroomData = partyroomRepository.findByPartyroomId(partyroomId.getId()).orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));
        Partyroom partyroom = partyroomConverter.toDomain(partyroomData);
        Partyroom updatedPartyroom = partyroom.updateMemberGrade(partymemberId, request.getGradeType());
        partyroomRepository.save(partyroomConverter.toData(updatedPartyroom));
    }

    public void updatePenalty(PartyroomId partyroomId, PartymemberId partymemberId, UpdateMemberPenaltyRequest request) {
        // TODO
    }
}
