package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.party.application.port.out.PlaylistQueryPort;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.entity.data.DjData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.service.PartyroomAggregateService;
import com.pfplaybackend.api.party.domain.specification.DjEnqueueSpecification;
import com.pfplaybackend.api.party.domain.value.*;
import com.pfplaybackend.api.party.domain.event.DjQueueChangedEvent;
import com.pfplaybackend.api.party.domain.exception.DjException;
import com.pfplaybackend.api.party.domain.exception.GradeException;
import com.pfplaybackend.api.party.adapter.out.persistence.DjRepository;
import com.pfplaybackend.api.party.adapter.out.persistence.PartyroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DjManagementService {

    private final PartyroomRepository partyroomRepository;
    private final DjRepository djRepository;
    private final PlaybackManagementService playbackManagementService;
    private final PlaylistQueryPort playlistQueryPort;
    private final ApplicationEventPublisher eventPublisher;
    private final PartyroomAggregateService partyroomAggregateService;
    private final PartyroomInfoService partyroomInfoService;

    @Transactional
    public void enqueueDj(PartyroomId partyroomId, PlaylistId playlistId)  {
        AuthContext authContext = ThreadLocalContext.getAuthContext();
        PartyroomData partyroom = partyroomInfoService.getPartyroomById(partyroomId);

        boolean isPostActivationProcessingRequired = !partyroom.isPlaybackActivated();
        boolean isAlreadyRegistered = djRepository.existsByPartyroomDataIdAndUserId(partyroomId.getId(), authContext.getUserId());
        boolean isEmptyPlaylist = playlistQueryPort.isEmptyPlaylist(playlistId.getId());
        new DjEnqueueSpecification().validate(partyroom, isAlreadyRegistered, isEmptyPlaylist);

        // Find crew
        CrewData crew = partyroomInfoService.getCrewOrThrow(partyroomId.getId(), authContext.getUserId());

        // Calculate next order number
        List<DjData> queuedDjs = djRepository.findByPartyroomDataIdOrderByOrderNumberAsc(partyroomId.getId());
        int nextOrder = queuedDjs.size() + 1;

        // Create and save DJ
        DjData dj = DjData.create(partyroom, playlistId, authContext.getUserId(), new CrewId(crew.getId()), nextOrder);
        djRepository.save(dj);

        partyroom.applyActivation();
        partyroomRepository.save(partyroom);

        eventPublisher.publishEvent(new DjQueueChangedEvent(partyroom.getPartyroomId()));

        if(isPostActivationProcessingRequired) {
            playbackManagementService.start(partyroom);
        }
    }

    @Transactional
    public void dequeueDj(PartyroomId partyroomId) {
        AuthContext authContext = ThreadLocalContext.getAuthContext();
        PartyroomData partyroom = partyroomInfoService.getPartyroomById(partyroomId);

        CrewData crew = partyroomInfoService.getCrewOrThrow(partyroomId.getId(), authContext.getUserId());
        CrewId crewId = new CrewId(crew.getId());

        boolean wasCurrentDj = partyroom.isPlaybackActivated() && partyroomAggregateService.isCurrentDj(partyroomId.getId(), crewId);
        partyroomAggregateService.removeDjFromQueue(partyroomId.getId(), crewId);

        eventPublisher.publishEvent(new DjQueueChangedEvent(partyroom.getPartyroomId()));
        if (wasCurrentDj) {
            playbackManagementService.skipBySystem(partyroomId);
        }
    }

    @Transactional
    public void dequeueDj(PartyroomId partyroomId, DjId djId) {
        AuthContext authContext = ThreadLocalContext.getAuthContext();
        PartyroomData partyroom = partyroomInfoService.getPartyroomById(partyroomId);

        // 관리자 등급 체크
        CrewData adjusterCrew = partyroomInfoService.getCrewOrThrow(partyroomId.getId(), authContext.getUserId());
        if (adjusterCrew.isBelowGrade(GradeType.MODERATOR))
            throw ExceptionCreator.create(GradeException.MANAGER_GRADE_REQUIRED);

        // 대상 DJ 조회
        DjData targetDj = djRepository.findById(djId.getId())
                .orElseThrow(() -> ExceptionCreator.create(DjException.NOT_FOUND_DJ));

        boolean wasCurrentDj = partyroom.isPlaybackActivated() && partyroomAggregateService.isCurrentDj(partyroomId.getId(), targetDj.getCrewId());
        partyroomAggregateService.removeDjFromQueue(partyroomId.getId(), targetDj.getCrewId());

        eventPublisher.publishEvent(new DjQueueChangedEvent(partyroom.getPartyroomId()));
        if (wasCurrentDj) {
            playbackManagementService.skipBySystem(partyroomId);
        }
    }

}
