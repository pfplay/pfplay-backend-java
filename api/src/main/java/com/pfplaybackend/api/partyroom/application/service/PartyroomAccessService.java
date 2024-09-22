package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.partyroom.application.aspect.context.PartyContext;
import com.pfplaybackend.api.partyroom.application.dto.ActivePartyroomWithCrewDto;
import com.pfplaybackend.api.partyroom.application.dto.CrewSummaryDto;
import com.pfplaybackend.api.partyroom.domain.entity.converter.PartyroomConverter;
import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Crew;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.partyroom.domain.enums.AccessType;
import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
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
import com.pfplaybackend.api.user.application.service.UserProfileService;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PartyroomAccessService {
    @Value("${shared-link.partyroom.web-page-url}")
    private String WEB_PAGE_URL;

    private final PartyroomRepository partyroomRepository;
    private final PartyroomConverter partyroomConverter;
    private final PartyroomDomainService partyroomDomainService;
    private final RedisMessagePublisher redisMessagePublisher;
    private final UserProfileService userProfileService;
    private final PartyroomInfoService partyroomInfoService;

    @Transactional
    public Crew tryEnter(PartyroomId partyroomId) {
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        UserId userId = partyContext.getUserId();
        AuthorityTier authorityTier = partyContext.getAuthorityTier();
        // Validate Partyroom Condition
        Optional<PartyroomData> optPartyroomData = partyroomRepository.findById(partyroomId.getId());
        if(optPartyroomData.isEmpty()) throw ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM);
        PartyroomData partyroomData = optPartyroomData.get();
        Partyroom partyroom = partyroomConverter.toDomain(partyroomData);
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

        Partyroom updPartyRoom = addOrActivateCrew(partyroom, userId, authorityTier);
        PartyroomData savedPartyRoomData = partyroomRepository.save(partyroomConverter.toData(updPartyRoom));
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
        redisMessagePublisher.publish(MessageTopic.PARTYROOM_ACCESS,
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
        PartyroomData partyroomData = partyroomRepository.findById(partyroomId.getId()).orElseThrow();
        Partyroom partyroom = partyroomConverter.toDomain(partyroomData);
        // TODO 퇴장 대상이 DJQueue 에 존재하는지 여부 확인
        // TODO 존재할 시 'Dj 대기열'에서 강제 제거
        //
        Crew crew = partyroom.deactivateCrewAndGet(partyContext.getUserId());
        partyroomRepository.save(partyroomConverter.toData(partyroom));

        CrewSummaryDto crewSummaryDto = new CrewSummaryDto(crew.getId());
        PartyroomAccessMessage partyroomAccessMessage = new PartyroomAccessMessage(
                partyroom.getPartyroomId(),
                MessageTopic.PARTYROOM_ACCESS,
                AccessType.EXIT,
                crewSummaryDto);
        redisMessagePublisher.publish(MessageTopic.PARTYROOM_ACCESS, partyroomAccessMessage);
    }

    @Transactional
    public void forceOut() {
        if(partyroomDomainService.isExistInDjQueue()) {
            // Dj 대기열에서 강제 제거
        }
        // TODO 퇴장 대상이 DJQueue 에 존재하는지 여부 확인
        // eventPublisher.publish(MessageTopic.PARTYROOM_ACCESS, updatedPartyroom);
    }

    @Transactional(readOnly = true)
    public URI getRedirectUri(String linkDomain) {
        PartyroomData partyroomData = partyroomRepository.findByLinkDomain(linkDomain)
                .orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));
        return UriComponentsBuilder
                .fromHttpUrl(WEB_PAGE_URL)
                .queryParam("partyroomId", partyroomData.getId())
                .build().toUri();
    }
}
