package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.partyroom.application.aspect.context.PartyContext;
import com.pfplaybackend.api.partyroom.application.dto.base.PartyroomDataDto;
import com.pfplaybackend.api.partyroom.application.dto.partyroom.ActivePartyroomWithCrewDto;
import com.pfplaybackend.api.partyroom.application.dto.crew.CrewSummaryDto;
import com.pfplaybackend.api.partyroom.application.peer.UserProfilePeerService;
import com.pfplaybackend.api.partyroom.domain.entity.converter.PartyroomConverter;
import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Crew;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.partyroom.domain.enums.AccessType;
import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import com.pfplaybackend.api.partyroom.domain.value.CrewId;
import com.pfplaybackend.api.partyroom.event.MessageTopic;
import com.pfplaybackend.api.partyroom.domain.service.PartyroomDomainService;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.config.redis.RedisMessagePublisher;
import com.pfplaybackend.api.partyroom.event.message.PartyroomAccessMessage;
import com.pfplaybackend.api.partyroom.exception.PartyroomException;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.partyroom.exception.PenaltyException;
import com.pfplaybackend.api.partyroom.repository.PartyroomRepository;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

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
        // Validate Partyroom Condition (Repeated)
        // FIXME Do not use 'findById' Method
        Optional<PartyroomData> optPartyroomData = partyroomRepository.findById(partyroomId.getId());
        if(optPartyroomData.isEmpty()) throw ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM);
        Partyroom partyroom = partyroomConverter.toDomain(optPartyroomData.get());
        if(partyroom.isTerminated()) throw ExceptionCreator.create(PartyroomException.ALREADY_TERMINATED);
        if(partyroom.isExceededLimit()) throw ExceptionCreator.create(PartyroomException.EXCEEDED_LIMIT);

        // Validate Crew Condition
        Optional<ActivePartyroomWithCrewDto> optActiveRoomInfo = partyroomInfoService.getMyActivePartyroomWithCrewId(userId);
        if (optActiveRoomInfo.isPresent()) {
            ActivePartyroomWithCrewDto activeRoomInfo = optActiveRoomInfo.get();
            if(partyroomDomainService.isActiveInAnotherRoom(partyroomId, new PartyroomId(activeRoomInfo.getId())))
                throw ExceptionCreator.create(PartyroomException.ACTIVE_ANOTHER_ROOM);
            return partyroom.getCrewByUserId(userId).orElseThrow();
        }

        Partyroom updatedPartyRoom = addOrActivateCrew(partyroom, userId, authorityTier);
        PartyroomData savedPartyRoomData = partyroomRepository.save(partyroomConverter.toData(updatedPartyRoom));
        // Publish Changed Event
        Crew crew = partyroomConverter.toDomain(savedPartyRoomData).getCrewByUserId(userId).orElseThrow();
        publishAccessChangedEvent(crew, userId);
        return crew;
    }

    private Partyroom addOrActivateCrew(Partyroom partyroom, UserId userId, AuthorityTier authorityTier) {
        if (partyroom.isUserInactiveCrew(userId)) {
            // Restore Existing Record
            if(partyroom.isUserBannedCrew(userId)) throw ExceptionCreator.create(PenaltyException.PERMANENT_EXPULSION);
            return partyroom.activateCrew(userId);
        }else {
            // Create New Record
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
        Optional<PartyroomDataDto> optional = partyroomRepository.findPartyroomDto(partyroomId);
        if(optional.isEmpty()) throw ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM);
        PartyroomData partyroomData =  partyroomConverter.toEntity(optional.get());
        Partyroom partyroom = partyroomConverter.toDomain(partyroomData);

        Crew crew = partyroom.deactivateCrewAndGet(partyContext.getUserId());
        // '퇴장하려는 크루'가 Dj 대기열에 존재한다면 강제 삭제(무효화)
        partyroom.tryRemoveInDjQueue(new CrewId(crew.getId()));
        partyroomRepository.save(partyroomConverter.toData(partyroom));

        // TODO 24.10.04 '퇴장하려는 크루'가 CurrentDj 인 경우에 한해, Current Playback 강제 스킵 처리
        // CurrentDj: orderNumber == 1
        if(partyroom.getCurrentDj().getCrewId().equals(new CrewId(crew.getId()))) {
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

    @Transactional(readOnly = true)
    public Map<String, Long> getRedirectUri(String linkDomain) {
        PartyroomData partyroomData = partyroomRepository.findByLinkDomain(linkDomain)
                .orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));
        return Map.of(
                "partyroomId", partyroomData.getId()
        );
    }
}
