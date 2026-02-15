package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.party.application.aspect.context.PartyContext;
import com.pfplaybackend.api.party.application.dto.base.PartyroomDataDto;
import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomWithCrewDto;
import com.pfplaybackend.api.party.application.dto.crew.CrewSummaryDto;
import com.pfplaybackend.api.party.application.peer.UserProfilePeerService;
import com.pfplaybackend.api.party.domain.entity.converter.PartyroomConverter;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.entity.domainmodel.Crew;
import com.pfplaybackend.api.party.domain.entity.domainmodel.Dj;
import com.pfplaybackend.api.party.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.party.application.dto.dj.DjWithProfileDto;
import com.pfplaybackend.api.party.domain.enums.AccessType;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.domain.service.PartyroomDomainService;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.common.config.redis.RedisMessagePublisher;
import com.pfplaybackend.api.party.interfaces.listener.redis.message.DjQueueChangeMessage;
import com.pfplaybackend.api.party.interfaces.listener.redis.message.PartyroomAccessMessage;
import com.pfplaybackend.api.party.domain.exception.CrewException;
import com.pfplaybackend.api.party.domain.exception.PartyroomException;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.party.domain.exception.PenaltyException;
import com.pfplaybackend.api.party.infrastructure.repository.PartyroomRepository;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartyroomAccessService {

    private final RedisMessagePublisher messagePublisher;
    private final PartyroomRepository partyroomRepository;
    private final PartyroomConverter partyroomConverter;
    private final PartyroomDomainService partyroomDomainService;
    private final PartyroomInfoService partyroomInfoService;
    private final UserProfilePeerService userProfileService;
    private final PlaybackManagementService playbackManagementService;

    @Transactional
    public Crew tryEnter(PartyroomId partyroomId) {
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        UserId userId = partyContext.getUserId();
        AuthorityTier authorityTier = partyContext.getAuthorityTier();
        log.info("[tryEnter] START - userId={}, targetPartyroomId={}, authorityTier={}",
                userId, partyroomId.getId(), authorityTier);

        // Validate Partyroom Condition (Repeated)
        // FIXME Do not use 'findById' Method
        Optional<PartyroomData> optPartyroomData = partyroomRepository.findById(partyroomId.getId());
        if(optPartyroomData.isEmpty()) {
            log.warn("[tryEnter] NOT_FOUND_ROOM - partyroomId={} does not exist in DB. userId={}",
                    partyroomId.getId(), userId);
            throw ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM);
        }
        Partyroom partyroom = partyroomConverter.toDomain(optPartyroomData.get());
        log.debug("[tryEnter] Partyroom found - partyroomId={}, isTerminated={}, crewCount={}, djCount={}",
                partyroomId.getId(), partyroom.isTerminated(),
                partyroom.getCrewSet().size(), partyroom.getDjSet().size());

        if(partyroom.isTerminated()) {
            log.warn("[tryEnter] ALREADY_TERMINATED - partyroomId={}, userId={}", partyroomId.getId(), userId);
            throw ExceptionCreator.create(PartyroomException.ALREADY_TERMINATED);
        }
        if(partyroom.isExceededLimit()) throw ExceptionCreator.create(PartyroomException.EXCEEDED_LIMIT);

        // Validate Crew Condition
        Optional<ActivePartyroomWithCrewDto> optActiveRoomInfo = partyroomInfoService.getMyActivePartyroomWithCrewId(userId);
        log.info("[tryEnter] Active room check - userId={}, hasActiveRoom={}, activeRoomId={}",
                userId,
                optActiveRoomInfo.isPresent(),
                optActiveRoomInfo.map(ActivePartyroomWithCrewDto::getId).orElse(null));

        if (optActiveRoomInfo.isPresent()) {
            ActivePartyroomWithCrewDto activeRoomInfo = optActiveRoomInfo.get();
            if(partyroomDomainService.isActiveInAnotherRoom(partyroomId, new PartyroomId(activeRoomInfo.getId()))) {
                // 기존 룸 자동 exit 처리 후 새 룸 enter 진행
                log.info("[tryEnter] Auto-exit from another room - userId={}, exitingRoomId={}, enteringRoomId={}",
                        userId, activeRoomInfo.getId(), partyroomId.getId());
                exit(new PartyroomId(activeRoomInfo.getId()));
            } else {
                // 같은 룸 재진입 — 아바타 이벤트를 다시 발행하여 다른 Crew에게 전파
                log.info("[tryEnter] Same room re-entry - userId={}, partyroomId={}", userId, partyroomId.getId());
                Crew crew = partyroom.getCrewByUserId(userId).orElseThrow();
                publishAccessChangedEvent(crew, userId);
                return crew;
            }
        }

        Partyroom updatedPartyRoom = addOrActivateCrew(partyroom, userId, authorityTier);
        PartyroomData savedPartyRoomData = partyroomRepository.save(partyroomConverter.toData(updatedPartyRoom));
        // Publish Changed Event
        Crew crew = partyroomConverter.toDomain(savedPartyRoomData).getCrewByUserId(userId).orElseThrow();
        log.info("[tryEnter] SUCCESS - userId={}, partyroomId={}, crewId={}", userId, partyroomId.getId(), crew.getId());
        publishAccessChangedEvent(crew, userId);
        return crew;
    }

    private Partyroom addOrActivateCrew(Partyroom partyroom, UserId userId, AuthorityTier authorityTier) {
        if (partyroom.isUserInactiveCrew(userId)) {
            // Restore Existing Record
            if(partyroom.isUserBannedCrew(userId)) {
                log.warn("[addOrActivateCrew] PERMANENT_EXPULSION - userId={}, partyroomId={}",
                        userId, partyroom.getPartyroomId().getId());
                throw ExceptionCreator.create(PenaltyException.PERMANENT_EXPULSION);
            }
            log.info("[addOrActivateCrew] Reactivating inactive crew - userId={}, partyroomId={}",
                    userId, partyroom.getPartyroomId().getId());
            return partyroom.activateCrew(userId);
        }else {
            // Create New Record
            log.info("[addOrActivateCrew] Adding new crew - userId={}, partyroomId={}, gradeType=LISTENER",
                    userId, partyroom.getPartyroomId().getId());
            return partyroom.addNewCrew(userId, authorityTier, GradeType.LISTENER);
        }
    }

    private void publishAccessChangedEvent(Crew crew, UserId userId) {
        ProfileSettingDto profileSettingDto = userProfileService.getUserProfileSetting(userId);
        messagePublisher.publish(MessageTopic.PARTYROOM_ACCESS,
                PartyroomAccessMessage.create(
                        crew.getPartyroomId(),
                        AccessType.ENTER,
                        CrewSummaryDto.from(crew, profileSettingDto)
                )
        );
    }

    @Transactional
    public void enterByHost(UserId hostId, Partyroom partyroom) {
        Partyroom updatedPartyroom = partyroom.addNewCrew(hostId, AuthorityTier.FM, GradeType.HOST);
        PartyroomData partyroomData = partyroomConverter.toData(updatedPartyroom);
        partyroomRepository.save(partyroomData);
    }

    @Transactional
    public void exit(PartyroomId partyroomId) {
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        log.info("[exit] START - userId={}, partyroomId={}", partyContext.getUserId(), partyroomId.getId());

        Optional<PartyroomDataDto> optional = partyroomRepository.findPartyroomDto(partyroomId);
        if(optional.isEmpty()) {
            log.warn("[exit] NOT_FOUND_ROOM - partyroomId={} not found via findPartyroomDto. userId={}",
                    partyroomId.getId(), partyContext.getUserId());
            throw ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM);
        }
        PartyroomData partyroomData =  partyroomConverter.toEntity(optional.get());
        Partyroom partyroom = partyroomConverter.toDomain(partyroomData);

        // 해당 방에 들어간 적이 없거나, 이미 나간 상황이라면 crewSet 에 없을 것이다.
        Optional<Crew> optionalCrew = partyroom.getCrewByUserId(partyContext.getUserId());
        if(optionalCrew.isEmpty()) {
            log.warn("[exit] INVALID_ACTIVE_ROOM - userId={} has no active crew in partyroomId={}. crewSetSize={}",
                    partyContext.getUserId(), partyroomId.getId(), partyroom.getCrewSet().size());
            throw ExceptionCreator.create(CrewException.INVALID_ACTIVE_ROOM);
        }

        Crew crew = partyroom.deactivateCrewAndGet(partyContext.getUserId());

        // '퇴장하려는 크루'가 Dj 대기열에 존재하는지 확인
        boolean wasInDjQueue = partyroom.getDjSet().stream()
                .anyMatch(dj -> dj.getCrewId().equals(new CrewId(crew.getId())) && dj.isQueued());

        // '퇴장하려는 크루'가 Dj 대기열에 존재한다면 강제 삭제(무효화)
        partyroom.tryRemoveInDjQueue(new CrewId(crew.getId()));
        partyroomRepository.save(partyroomConverter.toData(partyroom));

        if(wasInDjQueue) {
            publishDjQueueChangeEvent(partyroom);
        }

        // TODO 24.10.04 '퇴장하려는 크루'가 CurrentDj 인 경우에 한해, Current Playback 강제 스킵 처리
        // CurrentDj: orderNumber == 1
        Optional<Dj> optionalDj = partyroom.getCurrentDj();
        if(optionalDj.isPresent()) {
            if(optionalDj.get().getCrewId().equals(new CrewId(crew.getId()))) {
                playbackManagementService.skipBySystem(partyroomId);
            }
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
    public void expel(Partyroom partyroom, Crew crew, boolean isPermanent)  {
        // FIXME Parameter UserId → CrewId
        partyroom.deactivateCrewAndGet(crew.getUserId());
        // FIXME crew.getId() return type → CrewId
        if(isPermanent) partyroom.applyPermanentBan(new CrewId(crew.getId()));

        boolean wasInDjQueue = partyroom.getDjSet().stream()
                .anyMatch(dj -> dj.getCrewId().equals(new CrewId(crew.getId())) && dj.isQueued());
        boolean wasCurrentDj = partyroom.isCurrentDj(new CrewId(crew.getId()));

        partyroom.tryRemoveInDjQueue(new CrewId(crew.getId()));
        partyroomRepository.save(partyroomConverter.toData(partyroom));

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

    private void publishDjQueueChangeEvent(Partyroom partyroom) {
        messagePublisher.publish(MessageTopic.DJ_QUEUE_CHANGE,
                DjQueueChangeMessage.create(
                        partyroom.getPartyroomId(),
                        partyroomInfoService.getDjs(partyroom)
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
