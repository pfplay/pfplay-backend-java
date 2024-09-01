package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.partyroom.application.aspect.context.PartyContext;
import com.pfplaybackend.api.partyroom.domain.entity.converter.PartyroomConverter;
import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import com.pfplaybackend.api.partyroom.domain.service.CrewDomainService;
import com.pfplaybackend.api.partyroom.domain.service.PartyroomDomainService;
import com.pfplaybackend.api.partyroom.domain.value.PartymemberId;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.exception.GradeException;
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
    private final CrewDomainService crewDomainService;
    private final PartyroomAccessService partyroomAccessService;


    @Transactional
    public void updateGrade(PartyroomId partyroomId, PartymemberId partymemberId, UpdateMemberGradeRequest request) {
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        // TODO Exception 호출 멤버와 타겟 멤버는 같은 파티룸에 위치하는가?
        PartyroomData partyroomData = partyroomRepository.findByPartyroomId(partyroomId.getId()).orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));
        Partyroom partyroom = partyroomConverter.toDomain(partyroomData);
        if(request.getGradeType().equals(GradeType.HOST)) throw ExceptionCreator.create(GradeException.UNABLE_TO_SET_HOST);
        if(crewDomainService.isBelowManagerGrade(partyroom, partyContext.getUserId())) throw ExceptionCreator.create(GradeException.ADJUSTER_GRADE_NOT_MANAGER);
        if(crewDomainService.isAdjusterGradeLowerThanSubject(partyroom, partyContext.getUserId(), partymemberId)) throw ExceptionCreator.create(GradeException.ADJUSTER_GRADE_LOWER_THAN_SUBJECT);
        if(crewDomainService.isTargetGradeExceedingAdjuster(partyroom, partyContext.getUserId(), request.getGradeType())) throw ExceptionCreator.create(GradeException.TARGET_GRADE_HIGHER_THAN_ADJUSTER);
        Partyroom updatedPartyroom = partyroom.updateMemberGrade(partymemberId, request.getGradeType());
        partyroomRepository.save(partyroomConverter.toData(updatedPartyroom));
    }

    public void updatePenalty(PartyroomId partyroomId, PartymemberId partymemberId, UpdateMemberPenaltyRequest request) {
        // TODO
    }
}
