package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.partyroom.application.aspect.context.PartyContext;
import com.pfplaybackend.api.partyroom.domain.entity.converter.PartyroomConverter;
import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partymember;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.stream.DoubleStream;

@Service
@RequiredArgsConstructor
public class PartyroomManagementService {

    private final PartyroomRepository partyroomRepository;
    private final PartyroomConverter partyroomConverter;
    private final PartyroomDomainService partyroomDomainService;
    private final RedisMessagePublisher messagePublisher;

    public Partyroom createMainStage(CreatePartyroomRequest request) {
        String title = "메인 스테이지";
        String description = "이곳은 메인 스테이지입니다.";
        String suffixUri = "main";
        partyroomDomainService.checkIsLinkAddressDuplicated(suffixUri);
//        Partymember partymember = Partymember.create();
//        Partyroom partyroom = Partyroom.create(title, description, suffixUri, partymember, StageType.MAIN);
//        partyroomRepository.save(partyroom.toData());
        return null;
    }

    @Transactional
    public Partyroom createGeneralPartyRoom(CreatePartyroomRequest request) {
        try {
            PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
            partyroomDomainService.checkIsQualifiedToCreate(partyContext.getAuthorityTier());
            partyroomDomainService.checkIsLinkAddressDuplicated(request.getLinkDomain());
            // Create New Domain Object
            Partyroom partyroom = Partyroom.create(request, StageType.GENERAL, partyContext.getUserId());
            System.out.println(partyroom);
            PartyroomData partyroomData = partyroomConverter.toData(partyroom);
            PartyroomData savedPartyroomData = partyroomRepository.save(partyroomData);
            return partyroomConverter.toDomain(savedPartyroomData);
        }catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
        return null;
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
}
