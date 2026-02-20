package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.party.domain.entity.data.DjQueueData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomPlaybackData;
import com.pfplaybackend.api.party.domain.enums.QueueStatus;
import com.pfplaybackend.api.party.domain.enums.StageType;
import com.pfplaybackend.api.party.domain.policy.PartyroomCreationPolicy;
import com.pfplaybackend.api.party.domain.value.LinkDomain;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.value.PlaybackTimeLimit;
import com.pfplaybackend.api.party.domain.event.PartyroomClosedEvent;
import com.pfplaybackend.api.party.adapter.out.persistence.DjQueueRepository;
import com.pfplaybackend.api.party.adapter.out.persistence.PartyroomPlaybackRepository;
import com.pfplaybackend.api.party.adapter.out.persistence.PartyroomRepository;
import com.pfplaybackend.api.party.domain.exception.PartyroomException;
import com.pfplaybackend.api.party.adapter.in.web.payload.request.management.CreatePartyroomRequest;
import com.pfplaybackend.api.party.adapter.in.web.payload.request.management.UpdateDjQueueStatusRequest;
import com.pfplaybackend.api.party.adapter.in.web.payload.request.management.UpdatePartyroomRequest;
import com.pfplaybackend.api.common.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
    private final PartyroomPlaybackRepository partyroomPlaybackRepository;
    private final DjQueueRepository djQueueRepository;
    private final PartyroomAccessService partyroomAccessService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void createMainStage(CreatePartyroomRequest request, UserId adminId) {
        PartyroomData createdPartyroom = createPartyroom(request, StageType.MAIN, adminId);
        partyroomAccessService.enterByHost(adminId, createdPartyroom);
    }

    @Transactional
    public PartyroomData createGeneralPartyRoom(CreatePartyroomRequest request) {
        AuthContext authContext = ThreadLocalContext.getAuthContext();
        new PartyroomCreationPolicy().enforce(authContext.getAuthorityTier());
        Optional<PartyroomData> optionalActive = partyroomRepository.findActiveHostRoom(authContext.getUserId());
        if(optionalActive.isPresent()) throw ExceptionCreator.create(PartyroomException.ALREADY_HOST);

        if(request.getLinkDomain().isEmpty()) {
            request.setLinkDomain(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12));
        }
        PartyroomData createdPartyroom = createPartyroom(request, StageType.GENERAL, authContext.getUserId());
        partyroomAccessService.enterByHost(authContext.getUserId(), createdPartyroom);
        return createdPartyroom;
    }

    private PartyroomData createPartyroom(CreatePartyroomRequest request, StageType stageType, UserId hostId) {
        PartyroomData partyroom = PartyroomData.create(
                request.getTitle(), request.getIntroduction(),
                LinkDomain.of(request.getLinkDomain()),
                PlaybackTimeLimit.ofMinutes(request.getPlaybackTimeLimit()),
                stageType, hostId);
        PartyroomData saved = partyroomRepository.save(partyroom);
        partyroomPlaybackRepository.save(PartyroomPlaybackData.createFor(saved.getId()));
        djQueueRepository.save(DjQueueData.createFor(saved.getId()));
        return saved;
    }

    @Transactional
    public void updatePartyroom(PartyroomId partyroomId, UpdatePartyroomRequest request) {
        AuthContext authContext = ThreadLocalContext.getAuthContext();
        PartyroomData partyroom = partyroomRepository.findById(partyroomId.getId())
                .orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));
        partyroom.validateHost(authContext.getUserId());
        partyroom.updateBaseInfo(request.getTitle(), request.getIntroduction(),
                LinkDomain.of(request.getLinkDomain()),
                PlaybackTimeLimit.ofMinutes(request.getPlaybackTimeLimit()));
        partyroomRepository.save(partyroom);
    }

    @Transactional
    public void deletePartyRoom(PartyroomId partyroomId) {
        AuthContext authContext = ThreadLocalContext.getAuthContext();
        if (authContext.getAuthorityTier() != AuthorityTier.FM) {
            throw ExceptionCreator.create(PartyroomException.RESTRICTED_AUTHORITY);
        }
        PartyroomData partyroom = partyroomRepository.findById(partyroomId.getId())
                .orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));
        partyroom.terminate();
        partyroomRepository.save(partyroom);
        eventPublisher.publishEvent(new PartyroomClosedEvent(partyroom.getPartyroomId()));
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void deleteUnusedPartyroom() {
        List<PartyroomData> unusedPartyroomDataList = partyroomRepository.findAllUnusedPartyroomDataByDay(30);
        unusedPartyroomDataList.forEach(partyroom -> {
            partyroom.terminate();
            partyroomRepository.save(partyroom);
            eventPublisher.publishEvent(new PartyroomClosedEvent(partyroom.getPartyroomId()));
        });
    }

    @Transactional
    public void updateDjQueueStatus(PartyroomId partyroomId, UpdateDjQueueStatusRequest request) {
        AuthContext authContext = ThreadLocalContext.getAuthContext();
        PartyroomData partyroom = partyroomRepository.findById(partyroomId.getId())
                .orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));
        partyroom.validateHost(authContext.getUserId());
        DjQueueData djQueue = djQueueRepository.findById(partyroomId.getId()).orElseThrow();
        if (request.getQueueStatus().equals(QueueStatus.CLOSE)) djQueue.close();
        if (request.getQueueStatus().equals(QueueStatus.OPEN)) djQueue.open();
        djQueueRepository.save(djQueue);
    }

    public void initializeMainStage(UserId adminId) {
        CreatePartyroomRequest request = new CreatePartyroomRequest(
                "Main Stage",
                "Welcome to the main stage",
                "main",
                10);
        createMainStage(request, adminId);
    }
}
