package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomWithCrewDto;
import com.pfplaybackend.api.party.application.dto.crew.CrewSummaryDto;
import com.pfplaybackend.api.party.application.port.out.UserProfileQueryPort;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.entity.data.DjData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.application.dto.dj.DjWithProfileDto;
import com.pfplaybackend.api.party.domain.enums.AccessType;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.domain.service.PartyroomDomainService;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.common.config.redis.RedisMessagePublisher;
import com.pfplaybackend.api.party.adapter.in.listener.message.DjQueueChangeMessage;
import com.pfplaybackend.api.party.adapter.in.listener.message.PartyroomAccessMessage;
import com.pfplaybackend.api.party.domain.exception.CrewException;
import com.pfplaybackend.api.party.domain.exception.PartyroomException;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.party.domain.exception.PenaltyException;
import com.pfplaybackend.api.party.adapter.out.persistence.CrewRepository;
import com.pfplaybackend.api.party.adapter.out.persistence.DjRepository;
import com.pfplaybackend.api.party.adapter.out.persistence.PartyroomRepository;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartyroomAccessService {

    private final RedisMessagePublisher messagePublisher;
    private final PartyroomRepository partyroomRepository;
    private final CrewRepository crewRepository;
    private final DjRepository djRepository;
    private final PartyroomDomainService partyroomDomainService;
    private final PartyroomInfoService partyroomInfoService;
    private final UserProfileQueryPort userProfileService;
    private final PlaybackManagementService playbackManagementService;

    @Transactional
    public CrewData tryEnter(PartyroomId partyroomId) {
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
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
        log.debug("[tryEnter] Partyroom found - partyroomId={}, isTerminated={}, crewCount={}",
                partyroomId.getId(), partyroom.isTerminated(), activeCrewCount);

        if(partyroom.isTerminated()) {
            log.warn("[tryEnter] ALREADY_TERMINATED - partyroomId={}, userId={}", partyroomId.getId(), userId);
            throw ExceptionCreator.create(PartyroomException.ALREADY_TERMINATED);
        }
        if(activeCrewCount > 49) throw ExceptionCreator.create(PartyroomException.EXCEEDED_LIMIT);

        // Validate Crew Condition
        Optional<ActivePartyroomWithCrewDto> optActiveRoomInfo = partyroomInfoService.getMyActivePartyroomWithCrewId(userId);
        log.info("[tryEnter] Active room check - userId={}, hasActiveRoom={}, activeRoomId={}",
                userId,
                optActiveRoomInfo.isPresent(),
                optActiveRoomInfo.map(ActivePartyroomWithCrewDto::getId).orElse(null));

        if (optActiveRoomInfo.isPresent()) {
            ActivePartyroomWithCrewDto activeRoomInfo = optActiveRoomInfo.get();
            if(partyroomDomainService.isActiveInAnotherRoom(partyroomId, new PartyroomId(activeRoomInfo.getId()))) {
                log.info("[tryEnter] Auto-exit from another room - userId={}, exitingRoomId={}, enteringRoomId={}",
                        userId, activeRoomInfo.getId(), partyroomId.getId());
                exit(new PartyroomId(activeRoomInfo.getId()));
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
            if(crew.isBanned()) {
                log.warn("[addOrActivateCrew] PERMANENT_EXPULSION - userId={}, partyroomId={}",
                        userId, partyroom.getPartyroomId().getId());
                throw ExceptionCreator.create(PenaltyException.PERMANENT_EXPULSION);
            }
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
        ProfileSettingDto profileSettingDto = userProfileService.getUserProfileSetting(userId);
        messagePublisher.publish(MessageTopic.PARTYROOM_ACCESS,
                PartyroomAccessMessage.create(
                        partyroomId,
                        AccessType.ENTER,
                        CrewSummaryDto.from(crew, profileSettingDto)
                )
        );
    }

    @Transactional
    public void enterByHost(UserId hostId, PartyroomData partyroom) {
        CrewData crew = CrewData.create(partyroom, hostId, AuthorityTier.FM, GradeType.HOST);
        crewRepository.save(crew);
    }

    @Transactional
    public void exit(PartyroomId partyroomId) {
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
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

        CrewId crewId = new CrewId(crew.getId());
        boolean wasInDjQueue = djRepository.findByPartyroomDataIdAndCrewId(partyroomId.getId(), crewId)
                .map(DjData::isQueued).orElse(false);

        boolean wasCurrentDj = false;
        if (wasInDjQueue) {
            List<DjData> queuedDjs = djRepository.findByPartyroomDataIdAndIsQueuedTrueOrderByOrderNumberAsc(partyroomId.getId());
            wasCurrentDj = queuedDjs.stream()
                    .anyMatch(dj -> dj.getCrewId().equals(crewId) && dj.getOrderNumber() == 1);
        }

        removeFromDjQueue(partyroomId.getId(), crewId);

        if(wasInDjQueue) {
            publishDjQueueChangeEvent(partyroom);
        }

        if(wasCurrentDj) {
            playbackManagementService.skipBySystem(partyroomId);
        }

        CrewSummaryDto crewSummaryDto = new CrewSummaryDto(crew.getId());
        PartyroomAccessMessage partyroomAccessMessage = new PartyroomAccessMessage(
                partyroom.getPartyroomId(),
                MessageTopic.PARTYROOM_ACCESS,
                AccessType.EXIT,
                crewSummaryDto);
        messagePublisher.publish(MessageTopic.PARTYROOM_ACCESS, partyroomAccessMessage);
    }

    @Transactional
    public void expel(PartyroomData partyroom, CrewData crew, boolean isPermanent)  {
        crew.deactivatePresence();
        if(isPermanent) crew.enforceBan();
        crewRepository.save(crew);

        CrewId crewId = new CrewId(crew.getId());
        boolean wasInDjQueue = djRepository.findByPartyroomDataIdAndCrewId(partyroom.getId(), crewId)
                .map(DjData::isQueued).orElse(false);

        boolean wasCurrentDj = false;
        if (partyroom.isPlaybackActivated() && wasInDjQueue) {
            List<DjData> queuedDjs = djRepository.findByPartyroomDataIdAndIsQueuedTrueOrderByOrderNumberAsc(partyroom.getId());
            wasCurrentDj = queuedDjs.stream()
                    .anyMatch(dj -> dj.getCrewId().equals(crewId) && dj.getOrderNumber() == 1);
        }

        removeFromDjQueue(partyroom.getId(), crewId);

        if(wasInDjQueue) {
            publishDjQueueChangeEvent(partyroom);
        }

        if(wasCurrentDj) {
            playbackManagementService.skipBySystem(partyroom.getPartyroomId());
        }

        CrewSummaryDto crewSummaryDto = new CrewSummaryDto(crew.getId());
        PartyroomAccessMessage partyroomAccessMessage = new PartyroomAccessMessage(
                partyroom.getPartyroomId(),
                MessageTopic.PARTYROOM_ACCESS,
                AccessType.EXIT,
                crewSummaryDto);
        messagePublisher.publish(MessageTopic.PARTYROOM_ACCESS, partyroomAccessMessage);
    }

    private void removeFromDjQueue(Long partyroomId, CrewId crewId) {
        List<DjData> queuedDjs = djRepository.findByPartyroomDataIdAndIsQueuedTrueOrderByOrderNumberAsc(partyroomId);
        AtomicInteger order = new AtomicInteger(1);
        queuedDjs.forEach(dj -> {
            if (dj.getCrewId().equals(crewId)) {
                dj.applyDequeued();
            } else {
                dj.updateOrderNumber(order.getAndIncrement());
            }
        });
        djRepository.saveAll(queuedDjs);
    }

    private void publishDjQueueChangeEvent(PartyroomData partyroom) {
        messagePublisher.publish(MessageTopic.DJ_QUEUE_CHANGE,
                DjQueueChangeMessage.create(
                        partyroom.getPartyroomId(),
                        partyroomInfoService.getDjs(partyroom.getId())
                )
        );
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getRedirectUri(String linkDomain) {
        PartyroomData partyroomData = partyroomRepository.findByLinkDomain(linkDomain)
                .orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));
        return Map.of(
                "partyroomId", partyroomData.getId()
        );
    }
}
