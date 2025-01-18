package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.party.application.aspect.context.PartyContext;
import com.pfplaybackend.api.party.application.dto.base.PartyroomDataDto;
import com.pfplaybackend.api.party.application.peer.MusicQueryPeerService;
import com.pfplaybackend.api.party.domain.entity.converter.PartyroomConverter;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.entity.domainmodel.Crew;
import com.pfplaybackend.api.party.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.party.domain.service.PartyroomDomainService;
import com.pfplaybackend.api.party.domain.value.*;
import com.pfplaybackend.api.party.domain.exception.CrewException;
import com.pfplaybackend.api.party.domain.exception.DjException;
import com.pfplaybackend.api.party.domain.exception.PartyroomException;
import com.pfplaybackend.api.party.infrastructure.repository.PartyroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DjManagementService {

    private final PartyroomRepository partyroomRepository;
    private final PartyroomConverter partyroomConverter;
    private final PartyroomDomainService partyroomDomainService;
    private final PartyroomInfoService partyroomInfoService;
    private final PlaybackManagementService playbackManagementService;
    private final MusicQueryPeerService musicQueryService;

    @Transactional
    public void enqueueDj(PartyroomId partyroomId, PlaylistId playlistId)  {
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        // ActivePartyroomDto activePartyroom = partyroomInfoService.getMyActivePartyroom();
        // TODO Do not use 'findById'
        Optional<PartyroomDataDto> optional = partyroomRepository.findPartyroomDto(partyroomId);
        if(optional.isEmpty()) throw ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM);
        PartyroomDataDto partyroomDataDto = optional.get();
        PartyroomData partyroomData = partyroomConverter.toEntity(partyroomDataDto);
        Partyroom partyroom = partyroomConverter.toDomain(partyroomData);

        boolean isPostActivationProcessingRequired = !partyroom.isPlaybackActivated();
        if(partyroom.isQueueClosed()) throw ExceptionCreator.create(DjException.QUEUE_CLOSED);
        if(musicQueryService.isEmptyPlaylist(playlistId.getId())) throw ExceptionCreator.create(DjException.EMPTY_PLAYLIST);

        // FIXME Direct Add DjData to PartyroomData
        Partyroom updatedPartyroom = partyroom.createAndAddDj(playlistId, partyContext.getUserId()).applyActivation();
        PartyroomData updatedPartyroomData = partyroomRepository.save(partyroomConverter.toData(updatedPartyroom));

        if(isPostActivationProcessingRequired) {
            playbackManagementService.start(partyroomConverter.toDomain(updatedPartyroomData));
        }
    }

    /**
     * 대기열에 등록된 자신을 제거한다. (무효화한다.)
     * @param partyroomId
     */
    @Transactional
    public void dequeueDj(PartyroomId partyroomId) {
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        Optional<PartyroomDataDto> optional = partyroomRepository.findPartyroomDto(partyroomId);
        if(optional.isEmpty()) throw ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM);
        PartyroomDataDto partyroomDataDto = optional.get();
        PartyroomData partyroomData = partyroomConverter.toEntity(partyroomDataDto);
        Partyroom partyroom = partyroomConverter.toDomain(partyroomData);
        Optional<Crew> crewOptional = partyroom.getCrewByUserId(partyContext.getUserId());
        if(crewOptional.isEmpty()) throw ExceptionCreator.create(CrewException.NOT_FOUND_ACTIVE_ROOM);
        Crew crew = crewOptional.get();
        partyroom.tryRemoveInDjQueue(new CrewId(crew.getId()));
        partyroomRepository.save(partyroomConverter.toData(partyroom));
        // TODO 2024.10.06 CurrentDj인 경우, skipBySystem 호출
        playbackManagementService.skipBySystem(partyroomId);
    }

    /**
     * 대기열에 등록된 특정 Dj를 제거한다. (무효화한다.)
     * @param partyroomId
     * @param djId
     */
    @Transactional
    public void dequeueDj(PartyroomId partyroomId, DjId djId) {
        // TODO 관리자 등급 여부를 체크
    }
}
