package com.pfplaybackend.api.partyroom.application.service.dj;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.partyroom.application.aspect.context.PartyContext;
import com.pfplaybackend.api.partyroom.domain.entity.data.history.PlaybackReactionHistoryData;
import com.pfplaybackend.api.partyroom.repository.history.PlaybackReactionHistory;
import com.pfplaybackend.api.playlist.application.GrabMusicService;
import com.pfplaybackend.api.user.application.service.UserActivityService;
import com.pfplaybackend.api.user.domain.value.UserId;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DjMusicReactionService {

    private final PlaybackReactionHistory playbackReactionHistory;
    private final DjPlaybackService djPlaybackService;


    // TODO Call Other BoundaryContext
    private final GrabMusicService grabMusicService;
    private final UserActivityService userActivityService;

    @Transactional
    public void reactToCurrentPlayback() {
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        // 1. Reaction 이력 저장
        // TODO 좋아요/싫어요 토글로 동작해야 한다.
        PlaybackReactionHistoryData historyData = new PlaybackReactionHistoryData();
        playbackReactionHistory.save(historyData);
        // 2. Reaction 행위 결과를 Dj 에게 점수 반영
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