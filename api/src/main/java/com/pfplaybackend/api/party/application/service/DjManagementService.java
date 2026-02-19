package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.party.application.port.out.PlaylistQueryPort;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.entity.data.DjData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.service.PartyroomDomainService;
import com.pfplaybackend.api.party.domain.value.*;
import com.pfplaybackend.api.party.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.domain.exception.CrewException;
import com.pfplaybackend.api.party.domain.exception.DjException;
import com.pfplaybackend.api.party.domain.exception.GradeException;
import com.pfplaybackend.api.party.domain.exception.PartyroomException;
import com.pfplaybackend.api.party.domain.service.CrewDomainService;
import com.pfplaybackend.api.party.adapter.in.listener.message.DjQueueChangeMessage;
import com.pfplaybackend.api.party.adapter.out.persistence.CrewRepository;
import com.pfplaybackend.api.party.adapter.out.persistence.DjRepository;
import com.pfplaybackend.api.party.adapter.out.persistence.PartyroomRepository;
import com.pfplaybackend.api.common.config.redis.RedisMessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class DjManagementService {

    private final PartyroomRepository partyroomRepository;
    private final CrewRepository crewRepository;
    private final DjRepository djRepository;
    private final PartyroomDomainService partyroomDomainService;
    private final CrewDomainService crewDomainService;
    private final PartyroomInfoService partyroomInfoService;
    private final PlaybackManagementService playbackManagementService;
    private final PlaylistQueryPort musicQueryService;
    private final RedisMessagePublisher messagePublisher;

    @Transactional
    public void enqueueDj(PartyroomId partyroomId, PlaylistId playlistId)  {
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
        PartyroomData partyroom = partyroomRepository.findById(partyroomId.getId())
                .orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));

        boolean isPostActivationProcessingRequired = !partyroom.isPlaybackActivated();
        if(partyroom.isQueueClosed()) throw ExceptionCreator.create(DjException.QUEUE_CLOSED);
        if(musicQueryService.isEmptyPlaylist(playlistId.getId())) throw ExceptionCreator.create(DjException.EMPTY_PLAYLIST);

        // Check if already registered
        if(djRepository.existsByPartyroomDataIdAndUserIdAndIsQueuedTrue(partyroomId.getId(), authContext.getUserId())) {
            throw ExceptionCreator.create(DjException.ALREADY_REGISTERED);
        }

        // Find crew
        CrewData crew = crewRepository.findByPartyroomDataIdAndUserId(partyroomId.getId(), authContext.getUserId())
                .orElseThrow(() -> ExceptionCreator.create(CrewException.NOT_FOUND_ACTIVE_ROOM));

        // Calculate next order number
        List<DjData> queuedDjs = djRepository.findByPartyroomDataIdAndIsQueuedTrueOrderByOrderNumberAsc(partyroomId.getId());
        int nextOrder = queuedDjs.size() + 1;

        // Create and save DJ
        DjData dj = DjData.create(partyroom, playlistId, authContext.getUserId(), new CrewId(crew.getId()), nextOrder);
        djRepository.save(dj);

        partyroom.applyActivation();
        partyroomRepository.save(partyroom);

        publishDjQueueChangeEvent(partyroom);

        if(isPostActivationProcessingRequired) {
            playbackManagementService.start(partyroom);
        }
    }

    @Transactional
    public void dequeueDj(PartyroomId partyroomId) {
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
        PartyroomData partyroom = partyroomRepository.findById(partyroomId.getId())
                .orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));

        Optional<CrewData> crewOptional = crewRepository.findByPartyroomDataIdAndUserId(partyroomId.getId(), authContext.getUserId());
        if(crewOptional.isEmpty()) throw ExceptionCreator.create(CrewException.NOT_FOUND_ACTIVE_ROOM);
        CrewData crew = crewOptional.get();
        CrewId crewId = new CrewId(crew.getId());

        boolean wasCurrentDj = isCurrentDj(partyroomId.getId(), crewId, partyroom.isPlaybackActivated());
        removeFromDjQueue(partyroomId.getId(), crewId);

        publishDjQueueChangeEvent(partyroom);
        if (wasCurrentDj) {
            playbackManagementService.skipBySystem(partyroomId);
        }
    }

    @Transactional
    public void dequeueDj(PartyroomId partyroomId, DjId djId) {
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
        PartyroomData partyroom = partyroomRepository.findById(partyroomId.getId())
                .orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));

        // 관리자 등급 체크
        if(crewDomainService.isBelowManagerGrade(partyroomId.getId(), authContext.getUserId()))
            throw ExceptionCreator.create(GradeException.MANAGER_GRADE_REQUIRED);

        // 대상 DJ 조회
        DjData targetDj = djRepository.findById(djId.getId())
                .orElseThrow(() -> ExceptionCreator.create(DjException.NOT_FOUND_DJ));

        boolean wasCurrentDj = isCurrentDj(partyroomId.getId(), targetDj.getCrewId(), partyroom.isPlaybackActivated());
        removeFromDjQueue(partyroomId.getId(), targetDj.getCrewId());

        publishDjQueueChangeEvent(partyroom);
        if (wasCurrentDj) {
            playbackManagementService.skipBySystem(partyroomId);
        }
    }

    private boolean isCurrentDj(Long partyroomId, CrewId crewId, boolean isPlaybackActivated) {
        if (!isPlaybackActivated) return false;
        List<DjData> queuedDjs = djRepository.findByPartyroomDataIdAndIsQueuedTrueOrderByOrderNumberAsc(partyroomId);
        return queuedDjs.stream()
                .anyMatch(dj -> dj.getCrewId().equals(crewId) && dj.getOrderNumber() == 1);
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
        messagePublisher.publish(MessageTopic.DJ_QUEUE_CHANGE.topic(),
                DjQueueChangeMessage.create(
                        partyroom.getPartyroomId(),
                        partyroomInfoService.getDjs(partyroom.getId())
                )
        );
    }
}
