package com.pfplaybackend.api.partyroom.application.service.dj;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.partyroom.application.aspect.context.PartyContext;
import com.pfplaybackend.api.partyroom.application.dto.ActivePartyroomDto;
import com.pfplaybackend.api.partyroom.domain.entity.data.history.PlaybackReactionHistoryData;
import com.pfplaybackend.api.partyroom.domain.enums.ReactionType;
import com.pfplaybackend.api.partyroom.domain.service.PlaybackReactionDomainService;
import com.pfplaybackend.api.partyroom.domain.value.PlaybackId;
import com.pfplaybackend.api.partyroom.presentation.payload.request.ReactCurrentPlaybackRequest;
import com.pfplaybackend.api.partyroom.repository.PartyroomRepository;
import com.pfplaybackend.api.partyroom.repository.history.PlaybackReactionHistory;
import com.pfplaybackend.api.playlist.application.GrabMusicService;
import com.pfplaybackend.api.user.application.service.UserActivityService;
import com.pfplaybackend.api.user.domain.value.UserId;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class PlaybackReactionService {

    private final PartyroomRepository partyroomRepository;
    private final PlaybackReactionHistory playbackReactionHistory;
    private final PlaybackReactionDomainService playbackReactionDomainService;
    private final PlaybackService playbackService;

    // TODO Call Other BoundaryContext
    private final GrabMusicService grabMusicService;
    private final UserActivityService userActivityService;

    @Transactional
    public void reactToCurrentPlayback(ReactCurrentPlaybackRequest request) {
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        // 현재 Playback 객체 가져와서 Id 추출하기
        ActivePartyroomDto activePartyroom = partyroomRepository.getActivePartyroom(partyContext.getUserId()).orElseThrow();
        PlaybackId playbackId = activePartyroom.getCurrentPlaybackId();
        // 기존 이력 존재 여부 확인
        PlaybackReactionHistoryData historyData = playbackReactionHistory.findByPlaybackIdAndUserId(playbackId, partyContext.getUserId());

        if(historyData != null) {
            // '이력 있음' 시나리오
            try {
            // 복잡한 요구사항을 해결하기 위해 도메인 서비스 호출
                playbackReactionDomainService.determine(historyData, request.getReactionType());
            }catch (Exception e) {
                System.out.println(Arrays.toString(e.getStackTrace()));
            }
            // playbackReactionHistory.save(historyData);
        }else{
            // '이력 없음' 시나리오
            // 신규 레코드 저장
            PlaybackReactionHistoryData newHistoryData = new PlaybackReactionHistoryData();
            // playbackReactionHistory.save(newHistoryData);
        }
        // 2.


        // 3. Reaction 행위 결과를 Dj 에게 점수 반영
        // TODO (수정) Dj의 UserId 정보 조회
        userActivityService.updateDjPointScore(new UserId(), 1);
    }

    @Transactional
    public void grabCurrentPlayback() {
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        /// 1. Reaction 이력 저장
        // TODO Grabbed 토글로 동작해야 한다.
        PlaybackReactionHistoryData historyData = new PlaybackReactionHistoryData();
        playbackReactionHistory.save(historyData);
        // 2. Reaction 행위 결과를 Dj 에게 점수 반영
        // TODO (수정) Dj의 UserId 정보 조회
        userActivityService.updateDjPointScore(new UserId(), 2);
        // 3. 요청자의 Grab 플레이리스트에 현재 곡 정보를 저장
        // TODO 현재 곡 정보 조회
        grabMusicService.grabThisMusic();
    }

    // TODO '싫어요' 이력을 확인해야 한다.
    @Transactional
    public void getMyCurrentPlaybackReactionHistory() {
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        //
        // '리액션 취소' 요청은 그대로 반영하면 된다.
        // '리액션 반영' 요청은 연산 시점의 레코드 상태에 따라서 분기되어야 한다.
        // 일단, '레코드 조회'를 한 번 해야한다.
    }
}