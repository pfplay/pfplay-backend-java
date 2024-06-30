package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.partyroom.application.aspect.context.PartyContext;
import com.pfplaybackend.api.partyroom.domain.entity.converter.PartyroomConverter;
import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Dj;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.partyroom.domain.service.PartyroomDomainService;
import com.pfplaybackend.api.partyroom.domain.value.DjId;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.domain.value.PlaybackId;
import com.pfplaybackend.api.partyroom.domain.value.PlaylistId;
import com.pfplaybackend.api.partyroom.repository.PartyroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class DJManagementService {
    // Using Repositories
    private final PartyroomRepository partyroomRepository;
    // Using Entity Converters
    private final PartyroomConverter partyroomConverter;
    // Using Domain Services
    private final PartyroomDomainService partyroomDomainService;
    // Using Application Services
    private final PartyroomInfoService partyroomInfoService;
    private final PlaybackService playbackService;

    @Transactional
    public void enqueueDj(PartyroomId partyroomId, PlaylistId playlistId)  {
        System.out.println("EnqueueDj");
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        // ActivePartyroomDto activePartyroom = partyroomInfoService.getMyActivePartyroom();
        PartyroomData partyroomData = partyroomRepository.findById(partyroomId.getId()).orElseThrow();
        Partyroom partyroom = partyroomConverter.toDomain(partyroomData);

        boolean isPostActivationProcessingRequired = !partyroom.isPlaybackActivated();
        try {
            if(partyroom.isQueueClosed()) throw new Exception();
        }catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }

        // FIXME Direct Add DjData to PartyroomData
        partyroom.createAndAddDj(playlistId, partyContext.getUserId());
        partyroom.applyActivation();
        partyroomRepository.save(partyroomConverter.toData(partyroom));

        if(isPostActivationProcessingRequired) {
            playbackService.start(partyroom);
        }
    }

    /**
     * 대기열에 등록된 자신을 제거한다. (무효화한다.)
     * @param partyroomId
     */
    @Transactional
    public void dequeueDj(PartyroomId partyroomId) {
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
