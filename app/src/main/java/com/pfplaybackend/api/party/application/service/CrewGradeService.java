package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.application.port.out.UserProfileQueryPort;
import com.pfplaybackend.api.party.domain.event.CrewGradeChangedEvent;
import com.pfplaybackend.api.party.domain.specification.GradeAdjustmentSpecification;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.adapter.out.persistence.CrewRepository;
import com.pfplaybackend.api.party.domain.exception.CrewException;
import com.pfplaybackend.api.party.adapter.in.web.payload.request.regulation.AdjustGradeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CrewGradeService {

    private final ApplicationEventPublisher eventPublisher;
    private final CrewRepository crewRepository;
    private final PartyroomInfoService partyroomInfoService;
    private final UserProfileQueryPort userProfileQueryPort;

    @Transactional
    public void updateGrade(PartyroomId partyroomId, CrewId adjustedCrewId, AdjustGradeRequest request) {
        AuthContext authContext = ThreadLocalContext.getAuthContext();
        partyroomInfoService.getPartyroomById(partyroomId);

        CrewData adjustedCrew = crewRepository.findById(adjustedCrewId.getId())
                .orElseThrow(() -> ExceptionCreator.create(CrewException.NOT_FOUND_ACTIVE_ROOM));
        AuthorityTier authorityTier = userProfileQueryPort.getAuthorityTier(adjustedCrew.getUserId());
        GradeType prevGradeType = adjustedCrew.getGradeType();
        GradeType targetGradeType = request.getGradeType();
        CrewData adjusterCrew = partyroomInfoService.getCrewOrThrow(partyroomId.getId(), authContext.getUserId());

        new GradeAdjustmentSpecification().validate(adjusterCrew, adjustedCrew, targetGradeType, authorityTier);

        adjustedCrew.updateGrade(targetGradeType);
        crewRepository.save(adjustedCrew);

        eventPublisher.publishEvent(new CrewGradeChangedEvent(
                partyroomId, new CrewId(adjusterCrew.getId()), adjustedCrewId, prevGradeType, targetGradeType));
    }
}
