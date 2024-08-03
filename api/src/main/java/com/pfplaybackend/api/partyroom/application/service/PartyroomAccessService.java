package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.partyroom.application.aspect.context.PartyContext;
import com.pfplaybackend.api.partyroom.application.dto.ActivePartyroomWithMemberDto;
import com.pfplaybackend.api.partyroom.application.dto.PartymemberSummaryDto;
import com.pfplaybackend.api.partyroom.domain.entity.converter.PartyroomConverter;
import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partymember;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.partyroom.domain.enums.AccessType;
import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import com.pfplaybackend.api.partyroom.domain.enums.MessageTopic;
import com.pfplaybackend.api.partyroom.domain.service.PartyroomDomainService;
import com.pfplaybackend.api.partyroom.domain.value.PartymemberId;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.event.RedisMessagePublisher;
import com.pfplaybackend.api.partyroom.event.message.AccessMessage;
import com.pfplaybackend.api.partyroom.exception.PartyroomException;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.partyroom.exception.PenaltyException;
import com.pfplaybackend.api.partyroom.repository.PartyroomRepository;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import com.pfplaybackend.api.user.application.service.UserProfileService;
import com.pfplaybackend.api.user.domain.entity.domainmodel.User;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PartyroomAccessService {

    private final PartyroomRepository partyroomRepository;
    private final PartyroomConverter partyroomConverter;
    private final PartyroomDomainService partyroomDomainService;
    private final RedisMessagePublisher redisMessagePublisher;
    private final UserProfileService userProfileService;
    private final PartyroomInfoService partyroomInfoService;

    @Transactional
    public Partymember tryEnter(PartyroomId partyroomId) {
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        UserId userId = partyContext.getUserId();
        AuthorityTier authorityTier = partyContext.getAuthorityTier();
        // Validate Partyroom Condition
        // Include Inactive Members
        Optional<PartyroomData> optPartyroomData = partyroomRepository.findById(partyroomId.getId());
        if(optPartyroomData.isEmpty()) throw ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM);
        PartyroomData partyroomData = optPartyroomData.get();
        Partyroom partyroom = partyroomConverter.toDomain(partyroomData);
        if(partyroom.isTerminated()) throw ExceptionCreator.create(PartyroomException.ALREADY_TERMINATED);
        if(partyroom.isExceededLimit()) throw ExceptionCreator.create(PartyroomException.EXCEEDED_LIMIT);

        // Validate Crew(Partymember) Condition
        Optional<ActivePartyroomWithMemberDto> optActiveRoomInfo = partyroomInfoService.getMyActivePartyroomWithMemberId(userId);
        if (optActiveRoomInfo.isPresent()) {
            ActivePartyroomWithMemberDto activeRoomInfo = optActiveRoomInfo.get();
            if(partyroomDomainService.isActiveInAnotherRoom(partyroomId, new PartyroomId(activeRoomInfo.getId())))
                throw ExceptionCreator.create(PartyroomException.ACTIVE_ANOTHER_ROOM);
            return partyroom.getPartymemberByUserId(userId).orElseThrow();
        }

        Partyroom updPartyRoom = addOrActivateMember(partyroom, userId, authorityTier);
        PartyroomData savedPartyRoomData = partyroomRepository.save(partyroomConverter.toData(updPartyRoom));
        // Publish Changed Event
        Partymember partymember = partyroomConverter.toDomain(savedPartyRoomData).getPartymemberByUserId(userId).orElseThrow();
        publishAccessChangedEvent(partymember, userId);
        return partymember;
    }

    private Partyroom addOrActivateMember(Partyroom partyroom, UserId userId, AuthorityTier authorityTier) {
        if (partyroom.isUserInactiveMember(userId)) {
            // Restore Existing Record
            if(partyroom.isUserBannedMember(userId)) throw ExceptionCreator.create(PenaltyException.PERMANENT_EXPULSION);
            return partyroom.activatePartymember(userId);
        }else {
            // Create New Record
            return partyroom.addNewPartymember(userId, authorityTier, GradeType.LISTENER);
        }
    }

    private void publishAccessChangedEvent(Partymember partymember, UserId userId) {
        ProfileSettingDto profileSettingDto = userProfileService.getUserProfileSetting(userId);
        redisMessagePublisher.publish(MessageTopic.ACCESS,
                AccessMessage.create(
                        partymember.getPartyroomId(),
                        AccessType.ENTER,
                        PartymemberSummaryDto.from(partymember, profileSettingDto)
                )
        );
    }

    @Transactional
    public void enterByHost(UserId hostId, Partyroom partyroom) {
        Partyroom updatedPartyroom = partyroom.addNewPartymember(hostId, AuthorityTier.FM, GradeType.HOST);
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
        Partymember partymember = partyroom.deactivatePartymemberAndGet(partyContext.getUserId());
        partyroomRepository.save(partyroomConverter.toData(partyroom));

        PartymemberSummaryDto partymemberSummaryDto = new PartymemberSummaryDto(partymember.getId());
        AccessMessage accessMessage = new AccessMessage(
                partyroom.getPartyroomId(),
                MessageTopic.ACCESS,
                AccessType.EXIT,
                partymemberSummaryDto);
        redisMessagePublisher.publish(MessageTopic.ACCESS, accessMessage);
    }

    @Transactional
    public void forceOut() {
        if(partyroomDomainService.isExistInDjQueue()) {
            // Dj 대기열에서 강제 제거
        }
        // TODO 퇴장 대상이 DJQueue 에 존재하는지 여부 확인
        // eventPublisher.publish(MessageTopic.PARTYROOM_ACCESS, updatedPartyroom);
    }
}
