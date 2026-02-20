package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomWithCrewDto;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.entity.data.DjData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.service.PartyroomAggregateService;
import com.pfplaybackend.api.party.domain.enums.AccessType;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.event.CrewAccessedEvent;
import com.pfplaybackend.api.party.domain.event.DjQueueChangedEvent;
import com.pfplaybackend.api.party.domain.specification.PartyroomEntrySpecification;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.LinkDomain;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.exception.CrewException;
import com.pfplaybackend.api.party.domain.exception.PartyroomException;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.party.adapter.out.persistence.CrewRepository;
import com.pfplaybackend.api.party.adapter.out.persistence.DjRepository;
import com.pfplaybackend.api.party.adapter.out.persistence.PartyroomRepository;
import com.pfplaybackend.api.common.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartyroomAccessService {

    private final ApplicationEventPublisher eventPublisher;
    private final PartyroomRepository partyroomRepository;
    private final CrewRepository crewRepository;
    private final DjRepository djRepository;
    private final PartyroomAggregateService partyroomAggregateService;
    private final PartyroomInfoService partyroomInfoService;
    private final PlaybackManagementService playbackManagementService;

    @Transactional
    public CrewData tryEnter(PartyroomId partyroomId) {
        AuthContext authContext = ThreadLocalContext.getAuthContext();
        UserId userId = authContext.getUserId();
        AuthorityTier authorityTier = authContext.getAuthorityTier();
        log.info("[tryEnter] START - userId={}, targetPartyroomId={}, authorityTier={}",
                userId, partyroomId.getId(), authorityTier);

        PartyroomData partyroom = partyroomRepository.findById(partyroomId.getId())
                .orElseThrow(() -> {
                    log.warn("[tryEnter] NOT_FOUND_ROOM - partyroomId={} does not exist in DB. userId={}",
                            partyroomId.getId(), userId);
                    return ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM);
                });

        long activeCrewCount = crewRepository.countByPartyroomDataIdAndIsActiveTrue(partyroomId.getId());
        Optional<CrewData> existingCrew = crewRepository.findByPartyroomDataIdAndUserId(partyroomId.getId(), userId);
        log.debug("[tryEnter] Partyroom found - partyroomId={}, isTerminated={}, crewCount={}",
                partyroomId.getId(), partyroom.isTerminated(), activeCrewCount);

        new PartyroomEntrySpecification().validate(partyroom, activeCrewCount, existingCrew);

        // Validate Crew Condition
        Optional<ActivePartyroomWithCrewDto> optActiveRoomInfo = partyroomInfoService.getMyActivePartyroomWithCrewId(userId);
        log.info("[tryEnter] Active room check - userId={}, hasActiveRoom={}, activeRoomId={}",
                userId,
                optActiveRoomInfo.isPresent(),
                optActiveRoomInfo.map(ActivePartyroomWithCrewDto::id).orElse(null));

        if (optActiveRoomInfo.isPresent()) {
            ActivePartyroomWithCrewDto activeRoomInfo = optActiveRoomInfo.get();
            if(!partyroomId.equals(new PartyroomId(activeRoomInfo.id()))) {
                log.info("[tryEnter] Auto-exit from another room - userId={}, exitingRoomId={}, enteringRoomId={}",
                        userId, activeRoomInfo.id(), partyroomId.getId());
                exit(new PartyroomId(activeRoomInfo.id()));
            } else {
                log.info("[tryEnter] Same room re-entry - userId={}, partyroomId={}", userId, partyroomId.getId());
                CrewData crew = crewRepository.findByPartyroomDataIdAndUserId(partyroomId.getId(), userId)
                        .orElseThrow();
                publishAccessChangedEvent(partyroom.getPartyroomId(), crew, userId);
                return crew;
            }
        }

        CrewData crew = addOrActivateCrew(partyroom, userId, authorityTier);
        log.info("[tryEnter] SUCCESS - userId={}, partyroomId={}, crewId={}", userId, partyroomId.getId(), crew.getId());
        publishAccessChangedEvent(partyroom.getPartyroomId(), crew, userId);
        return crew;
    }

    private CrewData addOrActivateCrew(PartyroomData partyroom, UserId userId, AuthorityTier authorityTier) {
        Optional<CrewData> existingCrew = crewRepository.findByPartyroomDataIdAndUserId(partyroom.getId(), userId);
        if (existingCrew.isPresent() && !existingCrew.get().isActive()) {
            CrewData crew = existingCrew.get();
            log.info("[addOrActivateCrew] Reactivating inactive crew - userId={}, partyroomId={}",
                    userId, partyroom.getPartyroomId().getId());
            crew.activatePresence();
            return crewRepository.save(crew);
        } else {
            log.info("[addOrActivateCrew] Adding new crew - userId={}, partyroomId={}, gradeType=LISTENER",
                    userId, partyroom.getPartyroomId().getId());
            CrewData crew = CrewData.create(partyroom, userId, authorityTier, GradeType.LISTENER);
            return crewRepository.save(crew);
        }
    }

    private void publishAccessChangedEvent(PartyroomId partyroomId, CrewData crew, UserId userId) {
        eventPublisher.publishEvent(new CrewAccessedEvent(partyroomId, crew.getId(), userId, AccessType.ENTER));
    }

    @Transactional
    public void enterByHost(UserId hostId, PartyroomData partyroom) {
        CrewData crew = CrewData.create(partyroom, hostId, AuthorityTier.FM, GradeType.HOST);
        crewRepository.save(crew);
    }

    @Transactional
    public void exit(PartyroomId partyroomId) {
        AuthContext authContext = ThreadLocalContext.getAuthContext();
        log.info("[exit] START - userId={}, partyroomId={}", authContext.getUserId(), partyroomId.getId());

        PartyroomData partyroom = partyroomRepository.findById(partyroomId.getId())
                .orElseThrow(() -> {
                    log.warn("[exit] NOT_FOUND_ROOM - partyroomId={} not found. userId={}",
                            partyroomId.getId(), authContext.getUserId());
                    return ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM);
                });

        Optional<CrewData> optionalCrew = crewRepository.findByPartyroomDataIdAndUserId(partyroomId.getId(), authContext.getUserId());
        if(optionalCrew.isEmpty()) {
            log.warn("[exit] INVALID_ACTIVE_ROOM - userId={} has no active crew in partyroomId={}",
                    authContext.getUserId(), partyroomId.getId());
            throw ExceptionCreator.create(CrewException.INVALID_ACTIVE_ROOM);
        }

        CrewData crew = optionalCrew.get();
        crew.deactivatePresence();
        crewRepository.save(crew);

        handleDjQueueOnLeave(partyroom, new CrewId(crew.getId()));

        eventPublisher.publishEvent(new CrewAccessedEvent(partyroom.getPartyroomId(), crew.getId(), authContext.getUserId(), AccessType.EXIT));
    }

    @Transactional
    public void expel(PartyroomData partyroom, CrewData crew, boolean isPermanent)  {
        crew.deactivatePresence();
        if(isPermanent) crew.enforceBan();
        crewRepository.save(crew);

        handleDjQueueOnLeave(partyroom, new CrewId(crew.getId()));

        eventPublisher.publishEvent(new CrewAccessedEvent(partyroom.getPartyroomId(), crew.getId(), crew.getUserId(), AccessType.EXIT));
    }

    private void handleDjQueueOnLeave(PartyroomData partyroom, CrewId crewId) {
        boolean wasInDjQueue = djRepository.findByPartyroomDataIdAndCrewId(partyroom.getId(), crewId)
                .map(DjData::isQueued).orElse(false);
        boolean wasCurrentDj = partyroom.isPlaybackActivated() && wasInDjQueue
                && partyroomAggregateService.isCurrentDj(partyroom.getId(), crewId);

        partyroomAggregateService.removeDjFromQueue(partyroom.getId(), crewId);

        if (wasInDjQueue) {
            eventPublisher.publishEvent(new DjQueueChangedEvent(partyroom.getPartyroomId()));
        }
        if (wasCurrentDj) {
            playbackManagementService.skipBySystem(partyroom.getPartyroomId());
        }
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getRedirectUri(String linkDomain) {
        PartyroomData partyroomData = partyroomRepository.findByLinkDomain(LinkDomain.of(linkDomain))
                .orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));
        return Map.of(
                "partyroomId", partyroomData.getId()
        );
    }
}
