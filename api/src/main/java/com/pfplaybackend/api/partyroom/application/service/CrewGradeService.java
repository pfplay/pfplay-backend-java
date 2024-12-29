package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.partyroom.application.aspect.context.PartyContext;
import com.pfplaybackend.api.partyroom.application.dto.base.PartyroomDataDto;
import com.pfplaybackend.api.partyroom.domain.entity.converter.PartyroomConverter;
import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Crew;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import com.pfplaybackend.api.partyroom.event.MessageTopic;
import com.pfplaybackend.api.partyroom.domain.service.CrewDomainService;
import com.pfplaybackend.api.partyroom.domain.value.CrewId;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.config.redis.RedisMessagePublisher;
import com.pfplaybackend.api.partyroom.event.message.CrewGradeMessage;
import com.pfplaybackend.api.partyplay.exception.GradeException;
import com.pfplaybackend.api.partyroom.exception.PartyroomException;
import com.pfplaybackend.api.partyroom.presentation.payload.request.regulation.AdjustGradeRequest;
import com.pfplaybackend.api.partyroom.repository.PartyroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrewGradeService {

    private final RedisMessagePublisher messagePublisher;
    private final PartyroomRepository partyroomRepository;
    private final PartyroomConverter partyroomConverter;
    private final CrewDomainService crewDomainService;

    @Transactional
    public void updateGrade(PartyroomId partyroomId, CrewId adjustedCrewId, AdjustGradeRequest request) {
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        // TODO Exception 호출 멤버와 타겟 멤버는 같은 파티룸에 위치하는가?
        // TODO Extract Common Method
        Optional<PartyroomDataDto> optional = partyroomRepository.findPartyroomDto(partyroomId);
        if(optional.isEmpty()) throw ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM);
        PartyroomDataDto partyroomDataDto = optional.get();
        PartyroomData partyroomData = partyroomConverter.toEntity(partyroomDataDto);

        Partyroom partyroom = partyroomConverter.toDomain(partyroomData);
        Crew adjustedCrew = partyroom.getCrew(adjustedCrewId);
        AuthorityTier authorityTier = adjustedCrew.getAuthorityTier();
        GradeType prevGradeType = adjustedCrew.getGradeType();
        GradeType targetGradeType = request.getGradeType();
        Crew adjusterCrew = partyroom.getCrewByUserId(partyContext.getUserId()).orElseThrow();

        if(crewDomainService.isBelowManagerGrade(partyroom, partyContext.getUserId())) throw ExceptionCreator.create(GradeException.MANAGER_GRADE_REQUIRED);
        if(!request.getGradeType().equals(GradeType.LISTENER) && authorityTier.equals(AuthorityTier.GT)) throw ExceptionCreator.create(GradeException.GUEST_ONLY_POSSIBLE_LISTENER);
        if(request.getGradeType().equals(GradeType.HOST)) throw ExceptionCreator.create(GradeException.UNABLE_TO_SET_HOST);
        if(crewDomainService.isAdjusterGradeLowerThanSubject(partyroom, partyContext.getUserId(), adjustedCrewId)) throw ExceptionCreator.create(GradeException.GRADE_INSUFFICIENT_FOR_OPERATION);
        if(crewDomainService.isTargetGradeExceedingAdjuster(partyroom, partyContext.getUserId(), targetGradeType)) throw ExceptionCreator.create(GradeException.GRADE_EXCEEDS_ALLOWED_THRESHOLD);
        partyroom.updateCrewGrade(adjustedCrewId, targetGradeType);

        partyroomRepository.save(partyroomConverter.toData(partyroom));

        CrewGradeMessage message = CrewGradeMessage.from(partyroomId, new CrewId(adjusterCrew.getId()), adjustedCrewId, prevGradeType, targetGradeType);
        publishCrewGradeChangedEvent(message);
    }

    private void publishCrewGradeChangedEvent(CrewGradeMessage message) {
        messagePublisher.publish(MessageTopic.CREW_GRADE, message);
    }
}
