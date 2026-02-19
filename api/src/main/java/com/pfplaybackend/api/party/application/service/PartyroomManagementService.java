package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.domain.enums.StageType;
import com.pfplaybackend.api.party.domain.service.PartyroomDomainService;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.common.config.redis.RedisMessagePublisher;
import com.pfplaybackend.api.party.adapter.out.persistence.PartyroomRepository;
import com.pfplaybackend.api.party.adapter.in.listener.message.PartyroomClosedMessage;
import com.pfplaybackend.api.party.domain.exception.PartyroomException;
import com.pfplaybackend.api.party.adapter.in.web.payload.request.management.CreatePartyroomRequest;
import com.pfplaybackend.api.party.adapter.in.web.payload.request.management.UpdateDjQueueStatusRequest;
import com.pfplaybackend.api.party.adapter.in.web.payload.request.management.UpdatePartyroomRequest;
import com.pfplaybackend.api.common.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PartyroomManagementService {

    private final PartyroomRepository partyroomRepository;
    private final PartyroomDomainService partyroomDomainService;
    private final PartyroomAccessService partyroomAccessService;
    private final RedisMessagePublisher messagePublisher;

    @Transactional
    public void createMainStage(CreatePartyroomRequest request, UserId adminId) {
        PartyroomData createdPartyroom = createPartyroom(request, StageType.MAIN, adminId);
        partyroomAccessService.enterByHost(adminId, createdPartyroom);
    }

    @Transactional
    public PartyroomData createGeneralPartyRoom(CreatePartyroomRequest request) {
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
        partyroomDomainService.checkIsQualifiedToCreate(authContext.getAuthorityTier());
        Optional<PartyroomData> optionalActive = partyroomRepository.findActiveHostRoom(authContext.getUserId());
        if(optionalActive.isPresent()) throw ExceptionCreator.create(PartyroomException.ALREADY_HOST);

        if(request.getLinkDomain().isEmpty()) {
            request.setLinkDomain(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12));
        }
        partyroomDomainService.checkIsLinkAddressDuplicated(request.getLinkDomain());
        PartyroomData createdPartyroom = createPartyroom(request, StageType.GENERAL, authContext.getUserId());
        partyroomAccessService.enterByHost(authContext.getUserId(), createdPartyroom);
        return createdPartyroom;
    }

    private PartyroomData createPartyroom(CreatePartyroomRequest request, StageType stageType, UserId hostId) {
        PartyroomData partyroom = PartyroomData.create(request, stageType, hostId);
        return partyroomRepository.save(partyroom);
    }

    @Transactional
    public void updatePartyroom(PartyroomId partyroomId, UpdatePartyroomRequest request) {
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
        PartyroomData partyroom = partyroomRepository.findById(partyroomId.getId())
                .orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));
        partyroomDomainService.checkIsHost(partyroom, authContext.getUserId());
        partyroomDomainService.checkIsLinkAddressDuplicated(request.getLinkDomain());
        partyroom.updateBaseInfo(request);
        partyroomRepository.save(partyroom);
    }

    @Transactional
    public void deletePartyRoom(PartyroomId partyroomId) {
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
        if (authContext.getAuthorityTier() != AuthorityTier.FM) {
            throw ExceptionCreator.create(PartyroomException.RESTRICTED_AUTHORITY);
        }
        PartyroomData partyroom = partyroomRepository.findById(partyroomId.getId())
                .orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));
        partyroom.terminate();
        partyroomRepository.save(partyroom);
        messagePublisher.publish(
                MessageTopic.PARTYROOM_CLOSED.topic(),
                new PartyroomClosedMessage(partyroom.getPartyroomId(), MessageTopic.PARTYROOM_CLOSED)
        );
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void deleteUnusedPartyroom() {
        List<PartyroomData> unusedPartyroomDataList = partyroomRepository.findAllUnusedPartyroomDataByDay(30);
        unusedPartyroomDataList.forEach(partyroom -> {
            partyroom.terminate();
            partyroomRepository.save(partyroom);
            messagePublisher.publish(
                    MessageTopic.PARTYROOM_CLOSED.topic(),
                    new PartyroomClosedMessage(partyroom.getPartyroomId(), MessageTopic.PARTYROOM_CLOSED)
            );
        });
    }

    @Transactional
    public void updateDjQueueStatus(PartyroomId partyroomId, UpdateDjQueueStatusRequest request) {
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
        PartyroomData partyroom = partyroomRepository.findById(partyroomId.getId())
                .orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));
        partyroom.updatedQueueStatus(request);
        partyroomRepository.save(partyroom);
    }
}
