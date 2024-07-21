package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.partyroom.application.aspect.context.PartyContext;
import com.pfplaybackend.api.partyroom.domain.entity.converter.PartyroomConverter;
import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.partyroom.domain.enums.MessageTopic;
import com.pfplaybackend.api.partyroom.domain.enums.StageType;
import com.pfplaybackend.api.partyroom.domain.service.PartyroomDomainService;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.event.RedisMessagePublisher;
import com.pfplaybackend.api.partyroom.event.message.DeactivationMessage;
import com.pfplaybackend.api.partyroom.presentation.payload.request.CreatePartyroomRequest;
import com.pfplaybackend.api.partyroom.presentation.payload.request.UpdateDjQueueStatusRequest;
import com.pfplaybackend.api.partyroom.repository.PartyroomRepository;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class PartyroomManagementService {

    private final PartyroomRepository partyroomRepository;
    private final PartyroomConverter partyroomConverter;
    private final PartyroomDomainService partyroomDomainService;
    private final PartyroomAccessService partyroomAccessService;
    private final RedisMessagePublisher messagePublisher;

    @Transactional
    public void createMainStage(CreatePartyroomRequest request, UserId adminId) {
        Partyroom createdPartyroom = createPartyroom(request, StageType.MAIN, adminId);
        // Enter Partyroom
        partyroomAccessService.enterByHost(adminId, createdPartyroom);
    }

    @Transactional
    public Partyroom createGeneralPartyRoom(CreatePartyroomRequest request) {
        try {
            PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
            partyroomDomainService.checkIsQualifiedToCreate(partyContext.getAuthorityTier());
            partyroomDomainService.checkIsLinkAddressDuplicated(request.getLinkDomain());
            Partyroom createdPartyroom = createPartyroom(request, StageType.GENERAL, partyContext.getUserId());
            // Enter Partyroom
            partyroomAccessService.enterByHost(partyContext.getUserId(), createdPartyroom);
            return createdPartyroom;
        }catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
        return null;
    }

    private Partyroom createPartyroom(CreatePartyroomRequest request, StageType stageType, UserId hostId) {
        Partyroom partyroom = Partyroom.create(request, stageType, hostId);
        PartyroomData partyroomData = partyroomConverter.toData(partyroom);
        PartyroomData savedPartyroomData = partyroomRepository.save(partyroomData);
        // Enter Partyroom
        return partyroomConverter.toDomain(savedPartyroomData);
    }

    @Transactional
    public void deletePartyRoom(PartyroomId partyroomId) {
        // TODO 클라이언트가 파티룸의 호스트가 맞는가?
        // TODO 파티룸에 자신을 제외한 멤버가 없는가?
    }

    @Transactional
    public void updateDjQueueStatus(PartyroomId partyroomId, UpdateDjQueueStatusRequest request) {
    }

    @Transactional
    public void updatePlaybackActivationStatus(PartyroomId partyroomId, boolean isPlaybackActivated) {
        // TODO

        if(isPlaybackActivated) {

        }else {
            // Publish Message
            messagePublisher.publish(MessageTopic.DEACTIVATION, new DeactivationMessage(partyroomId, MessageTopic.DEACTIVATION));
        }
    }

    @Transactional
    public Partyroom rotateDjQueue(Partyroom partyroom) {
        PartyroomData partyroomData = partyroomRepository.save(partyroomConverter.toData(partyroom.rotateDjs()));
        return partyroomConverter.toDomain(partyroomData);
    }
}