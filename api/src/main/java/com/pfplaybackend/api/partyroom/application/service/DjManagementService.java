package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.partyroom.application.aspect.context.PartyContext;
import com.pfplaybackend.api.partyroom.application.peer.MusicQueryPeerService;
import com.pfplaybackend.api.partyroom.domain.entity.converter.PartyroomConverter;
import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Dj;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.partyroom.domain.service.PartyroomDomainService;
import com.pfplaybackend.api.partyroom.domain.value.DjId;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.domain.value.PlaybackId;
import com.pfplaybackend.api.partyroom.domain.value.PlaylistId;
import com.pfplaybackend.api.partyroom.exception.DjException;
import com.pfplaybackend.api.partyroom.repository.PartyroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

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
        PartyroomData partyroomData = partyroomRepository.findById(partyroomId.getId()).orElseThrow();
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
        // 자신을 삭제시키려면, 자신의 dj 번호를 조회해야 한다.
        // userId → deleted_yn == 0 인 dj 레코드를 deleted_yn == 1로 갱신
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

    public Dj getDjByPlayback(PlaybackId playbackId) {
        return new Dj();
    }
}
