package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.partyroom.application.aspect.context.PartyContext;
import com.pfplaybackend.api.partyroom.domain.entity.converter.PartyroomConverter;
import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import com.pfplaybackend.api.partyroom.event.MessageTopic;
import com.pfplaybackend.api.partyroom.domain.service.CrewDomainService;
import com.pfplaybackend.api.partyroom.domain.value.CrewId;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.config.redis.RedisMessagePublisher;
import com.pfplaybackend.api.partyroom.event.message.CrewGradeMessage;
import com.pfplaybackend.api.partyroom.exception.GradeException;
import com.pfplaybackend.api.partyroom.exception.PartyroomException;
import com.pfplaybackend.api.partyroom.presentation.payload.request.regulation.AdjustGradeRequest;
import com.pfplaybackend.api.partyroom.repository.PartyroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CrewGradeService {

    private final RedisMessagePublisher redisMessagePublisher;;
    private final PartyroomRepository partyroomRepository;
    private final PartyroomConverter partyroomConverter;
    private final CrewDomainService crewDomainService;

    @Transactional
    public void updateGrade(PartyroomId partyroomId, CrewId crewId, AdjustGradeRequest request) {
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        // TODO Exception 호출 멤버와 타겟 멤버는 같은 파티룸에 위치하는가?
        PartyroomData partyroomData = partyroomRepository.findByPartyroomId(partyroomId.getId()).orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));
        Partyroom partyroom = partyroomConverter.toDomain(partyroomData);
        GradeType prevGradeType = partyroom.getCrew(crewId).getGradeType();
        GradeType targetGradeType = request.getGradeType();

        if(crewDomainService.isBelowManagerGrade(partyroom, partyContext.getUserId())) throw ExceptionCreator.create(GradeException.MANAGER_GRADE_REQUIRED);
        if(request.getGradeType().equals(GradeType.HOST)) throw ExceptionCreator.create(GradeException.UNABLE_TO_SET_HOST);
        if(crewDomainService.isAdjusterGradeLowerThanSubject(partyroom, partyContext.getUserId(), crewId)) throw ExceptionCreator.create(GradeException.GRADE_INSUFFICIENT_FOR_OPERATION);
        if(crewDomainService.isTargetGradeExceedingAdjuster(partyroom, partyContext.getUserId(), targetGradeType)) throw ExceptionCreator.create(GradeException.GRADE_EXCEEDS_ALLOWED_THRESHOLD);
        Partyroom updatedPartyroom = partyroom.updateCrewGrade(crewId, targetGradeType);
        partyroomRepository.save(partyroomConverter.toData(updatedPartyroom));

        CrewGradeMessage message = CrewGradeMessage.from(partyroomId, crewId, prevGradeType, targetGradeType);
        publishCrewGradeChangedEvent(message);
    }

    private void publishCrewGradeChangedEvent(CrewGradeMessage message) {
        redisMessagePublisher.publish(MessageTopic.CREW_GRADE, message);
    }
}
