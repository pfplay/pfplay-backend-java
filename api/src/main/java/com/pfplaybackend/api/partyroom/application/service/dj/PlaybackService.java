package com.pfplaybackend.api.partyroom.application.service.dj;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.partyroom.application.aspect.context.PartyContext;
import com.pfplaybackend.api.partyroom.application.dto.ActivePartyroomDto;
import com.pfplaybackend.api.partyroom.application.service.PartyroomInfoService;
import com.pfplaybackend.api.partyroom.application.service.PartyroomManagementService;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Playback;
import com.pfplaybackend.api.partyroom.domain.service.DjDomainService;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.domain.value.PlaybackId;
import com.pfplaybackend.api.partyroom.repository.PlaybackRepository;
import com.pfplaybackend.api.playlist.application.PlaylistMusicService;
import com.pfplaybackend.api.user.application.service.UserActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaybackService {

    //
    private final PlaybackRepository playbackRepository;
    //
    private final DjDomainService djDomainService;
    //
    private final PartyroomManagementService partyroomManagementService;
    private final PartyroomInfoService partyroomInfoService;

    // TODO Problem: Call method outside of bounded context
    private final PlaylistMusicService playlistMusicService;
    private final UserActivityService userActivityService;

    @Transactional
    public void complete(PartyroomId partyroomId) {
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        // TODO 리액션 점수 반영은 실시간으로 반영해야 한다.
        // userActivityService.updateDjPointScore(UUID.randomUUID(), 1);
        // Partyroom partyroom = new Partyroom();
        // partyroom.getNextDJ();
        if(djDomainService.isExistNextDj()) {
            Playback nextPlayback = getNextPlayback();
        }else{
            // 파티룸의 '재생 활성화'상태 끄기
            partyroomManagementService.updatePlaybackActivationStatus(partyroomId, false);
        }
    }

    public void skip(PartyroomId partyroomId) {
        // Partyroom partyroom = new Partyroom();
        // 다음 DJ 존재하면 start();
        // 없으면 파티룸 비활성화
        partyroomManagementService.updatePlaybackActivationStatus(partyroomId, false);
    }


    public void start() {

    }

    public Playback getNextPlayback() {
        // 1. Dj 대기열 내 Dj 들의 orderNumber 일괄 변환
        // 2. Playback 객체 생성
        Playback playback = new Playback();
        return playback;
    }

    public PlaybackId getCurrentPlaybackId() {
        return partyroomInfoService.getActivePartyroom().getCurrentPlaybackId();
    }

    public Playback updateLikeCount(PlaybackId playbackId) {
        // TODO How to increment '1'
        return new Playback();
    }
}
