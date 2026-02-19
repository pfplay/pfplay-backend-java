package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.domain.service.CrewDomainService;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.common.config.redis.RedisMessagePublisher;
import com.pfplaybackend.api.party.interfaces.listener.redis.message.CrewGradeMessage;
import com.pfplaybackend.api.party.domain.exception.GradeException;
import com.pfplaybackend.api.party.domain.exception.PartyroomException;
import com.pfplaybackend.api.party.interfaces.api.rest.payload.request.regulation.AdjustGradeRequest;
import com.pfplaybackend.api.party.infrastructure.repository.PartyroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CrewGradeService {

    private final RedisMessagePublisher messagePublisher;
    private final PartyroomRepository partyroomRepository;
    private final CrewDomainService crewDomainService;

    @Transactional
    public void updateGrade(PartyroomId partyroomId, CrewId adjustedCrewId, AdjustGradeRequest request) {
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
        PartyroomData partyroom = partyroomRepository.findById(partyroomId.getId())
                .orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));

        CrewData adjustedCrew = partyroom.getCrew(adjustedCrewId);
        AuthorityTier authorityTier = adjustedCrew.getAuthorityTier();
        GradeType prevGradeType = adjustedCrew.getGradeType();
        GradeType targetGradeType = request.getGradeType();
        CrewData adjusterCrew = partyroom.getCrewByUserId(authContext.getUserId()).orElseThrow();

        if(crewDomainService.isBelowManagerGrade(partyroom, authContext.getUserId())) throw ExceptionCreator.create(GradeException.MANAGER_GRADE_REQUIRED);
        if(!request.getGradeType().equals(GradeType.LISTENER) && authorityTier.equals(AuthorityTier.GT)) throw ExceptionCreator.create(GradeException.GUEST_ONLY_POSSIBLE_LISTENER);
        if(request.getGradeType().equals(GradeType.HOST)) throw ExceptionCreator.create(GradeException.UNABLE_TO_SET_HOST);
        if(crewDomainService.isAdjusterGradeLowerThanSubject(partyroom, authContext.getUserId(), adjustedCrewId)) throw ExceptionCreator.create(GradeException.GRADE_INSUFFICIENT_FOR_OPERATION);
        if(crewDomainService.isTargetGradeExceedingAdjuster(partyroom, authContext.getUserId(), targetGradeType)) throw ExceptionCreator.create(GradeException.GRADE_EXCEEDS_ALLOWED_THRESHOLD);
        partyroom.updateCrewGrade(adjustedCrewId, targetGradeType);

        partyroomRepository.save(partyroom);

        CrewGradeMessage message = CrewGradeMessage.from(partyroomId, new CrewId(adjusterCrew.getId()), adjustedCrewId, prevGradeType, targetGradeType);
        publishCrewGradeChangedEvent(message);
    }

    private void publishCrewGradeChangedEvent(CrewGradeMessage message) {
        messagePublisher.publish(MessageTopic.CREW_GRADE, message);
    }
}
