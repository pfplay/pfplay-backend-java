package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.party.application.peer.MusicQueryPeerService;
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
import com.pfplaybackend.api.party.interfaces.listener.redis.message.DjQueueChangeMessage;
import com.pfplaybackend.api.party.infrastructure.repository.PartyroomRepository;
import com.pfplaybackend.api.common.config.redis.RedisMessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DjManagementService {

    private final PartyroomRepository partyroomRepository;
    private final PartyroomDomainService partyroomDomainService;
    private final CrewDomainService crewDomainService;
    private final PartyroomInfoService partyroomInfoService;
    private final PlaybackManagementService playbackManagementService;
    private final MusicQueryPeerService musicQueryService;
    private final RedisMessagePublisher messagePublisher;

    @Transactional
    public void enqueueDj(PartyroomId partyroomId, PlaylistId playlistId)  {
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
        PartyroomData partyroom = partyroomRepository.findById(partyroomId.getId())
                .orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));

        boolean isPostActivationProcessingRequired = !partyroom.isPlaybackActivated();
        if(partyroom.isQueueClosed()) throw ExceptionCreator.create(DjException.QUEUE_CLOSED);
        if(musicQueryService.isEmptyPlaylist(playlistId.getId())) throw ExceptionCreator.create(DjException.EMPTY_PLAYLIST);

        partyroom.createAndAddDj(playlistId, authContext.getUserId()).applyActivation();
        PartyroomData savedPartyroom = partyroomRepository.save(partyroom);
        publishDjQueueChangeEvent(savedPartyroom);

        if(isPostActivationProcessingRequired) {
            playbackManagementService.start(savedPartyroom);
        }
    }

    @Transactional
    public void dequeueDj(PartyroomId partyroomId) {
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
        PartyroomData partyroom = partyroomRepository.findById(partyroomId.getId())
                .orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));
        Optional<CrewData> crewOptional = partyroom.getCrewByUserId(authContext.getUserId());
        if(crewOptional.isEmpty()) throw ExceptionCreator.create(CrewException.NOT_FOUND_ACTIVE_ROOM);
        CrewData crew = crewOptional.get();
        boolean wasCurrentDj = partyroom.isCurrentDj(new CrewId(crew.getId()));
        partyroom.tryRemoveInDjQueue(new CrewId(crew.getId()));
        partyroomRepository.save(partyroom);
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
        if(crewDomainService.isBelowManagerGrade(partyroom, authContext.getUserId()))
            throw ExceptionCreator.create(GradeException.MANAGER_GRADE_REQUIRED);
        // 대상 DJ 조회
        DjData targetDj = partyroom.getDjById(djId.getId())
                .orElseThrow(() -> ExceptionCreator.create(DjException.NOT_FOUND_DJ));
        boolean wasCurrentDj = partyroom.isCurrentDj(targetDj.getCrewId());
        partyroom.tryRemoveInDjQueue(targetDj.getCrewId());
        partyroomRepository.save(partyroom);
        publishDjQueueChangeEvent(partyroom);
        if (wasCurrentDj) {
            playbackManagementService.skipBySystem(partyroomId);
        }
    }

    private void publishDjQueueChangeEvent(PartyroomData partyroom) {
        messagePublisher.publish(MessageTopic.DJ_QUEUE_CHANGE,
                DjQueueChangeMessage.create(
                        partyroom.getPartyroomId(),
                        partyroomInfoService.getDjs(partyroom)
                )
        );
    }
}
