package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.party.application.aspect.context.PartyContext;
import com.pfplaybackend.api.party.application.dto.base.PartyroomDataDto;
import com.pfplaybackend.api.party.domain.entity.converter.PartyroomConverter;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.party.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.domain.enums.StageType;
import com.pfplaybackend.api.party.domain.service.PartyroomDomainService;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.config.redis.RedisMessagePublisher;
import com.pfplaybackend.api.party.infrastructure.repository.PartyroomRepository;
import com.pfplaybackend.api.party.interfaces.listener.redis.message.PartyroomDeactivationMessage;
import com.pfplaybackend.api.party.domain.exception.PartyroomException;
import com.pfplaybackend.api.party.interfaces.api.rest.payload.request.management.CreatePartyroomRequest;
import com.pfplaybackend.api.party.interfaces.api.rest.payload.request.management.UpdateDjQueueStatusRequest;
import com.pfplaybackend.api.party.interfaces.api.rest.payload.request.management.UpdatePartyroomRequest;
import com.pfplaybackend.api.user.domain.value.UserId;
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
        // Create Partyroom
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        partyroomDomainService.checkIsQualifiedToCreate(partyContext.getAuthorityTier());
        Optional<PartyroomData> optionalActive = partyroomRepository.findActiveHostRoom(partyContext.getUserId());
        if(optionalActive.isPresent()) throw ExceptionCreator.create(PartyroomException.ALREADY_HOST);

        // TODO 고유성 검증 구현 필요
        if(request.getLinkDomain().isEmpty()) {
            request.setLinkDomain(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12));
        }
        partyroomDomainService.checkIsLinkAddressDuplicated(request.getLinkDomain());
        Partyroom createdPartyroom = createPartyroom(request, StageType.GENERAL, partyContext.getUserId());
        // Enter Partyroom
        partyroomAccessService.enterByHost(partyContext.getUserId(), createdPartyroom);
        return createdPartyroom;
    }

    private Partyroom createPartyroom(CreatePartyroomRequest request, StageType stageType, UserId hostId) {
        Partyroom partyroom = Partyroom.create(request, stageType, hostId);
        PartyroomData partyroomData = partyroomConverter.toData(partyroom);
        PartyroomData savedPartyroomData = partyroomRepository.save(partyroomData);
        // Enter Partyroom
        return partyroomConverter.toDomain(savedPartyroomData);
    }

    @Transactional
    public void updatePartyroom(PartyroomId partyroomId, UpdatePartyroomRequest request) {
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        // FIXME Extract Common Method
        Optional<PartyroomDataDto> optional = partyroomRepository.findPartyroomDto(partyroomId);
        if(optional.isEmpty()) throw ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM);
        PartyroomDataDto partyroomDataDto = optional.get();
        PartyroomData partyroomData = partyroomConverter.toEntity(partyroomDataDto);
        Partyroom partyroom = partyroomConverter.toDomain(partyroomData);
        // Domain Logic
        partyroomDomainService.checkIsHost(partyroom, partyContext.getUserId());
        partyroomDomainService.checkIsLinkAddressDuplicated(request.getLinkDomain());
        partyroomRepository.save(partyroomConverter.toData(partyroom.updateBaseInfo(request)));
    }

    @Transactional
    public void deletePartyRoom(PartyroomId partyroomId) {
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        if (partyContext.getAuthorityTier() != AuthorityTier.FM) {
            throw ExceptionCreator.create(PartyroomException.RESTRICTED_AUTHORITY);
        }
        PartyroomData partyroomData = partyroomRepository.findById(partyroomId.getId())
                .orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));
        Partyroom partyroom = partyroomConverter.toDomain(partyroomData);
        partyroom.terminate();
        partyroomRepository.save(partyroomConverter.toData(partyroom));
        messagePublisher.publish(
                MessageTopic.PARTYROOM_CLOSED,
                new PartyroomDeactivationMessage(partyroom.getPartyroomId(), MessageTopic.PARTYROOM_CLOSED)
        );
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void deleteUnusedPartyroom() {
        List<PartyroomData> unusedPartyroomDataList = partyroomRepository.findAllUnusedPartyroomDataByDay(30);
        unusedPartyroomDataList.forEach(partyroomData -> {
            Partyroom partyroom = partyroomConverter.toDomain(partyroomData);
            partyroom.terminate();
            partyroomRepository.save(partyroomConverter.toData(partyroom));
            messagePublisher.publish(
                    MessageTopic.PARTYROOM_CLOSED,
                    new PartyroomDeactivationMessage(partyroom.getPartyroomId(), MessageTopic.PARTYROOM_CLOSED)
            );
        });
    }

    @Transactional
    public void updateDjQueueStatus(PartyroomId partyroomId, UpdateDjQueueStatusRequest request) {
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        // FIXME Extract Common Method
        Optional<PartyroomDataDto> optional = partyroomRepository.findPartyroomDto(partyroomId);
        if(optional.isEmpty()) throw ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM);
        PartyroomDataDto partyroomDataDto = optional.get();
        PartyroomData partyroomData = partyroomConverter.toEntity(partyroomDataDto);
        Partyroom partyroom = partyroomConverter.toDomain(partyroomData);

        // TODO 권한 검증
        partyroom.updatedQueueStatus(request);
        partyroomRepository.save(partyroomConverter.toData(partyroom));
    }
}