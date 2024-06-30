package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.partyroom.application.aspect.context.PartyContext;
import com.pfplaybackend.api.partyroom.application.dto.ActivePartyroomDto;
import com.pfplaybackend.api.partyroom.application.dto.AggregationDto;
import com.pfplaybackend.api.partyroom.application.peer.GrabMusicPeerService;
import com.pfplaybackend.api.partyroom.application.peer.UserActivityPeerService;
import com.pfplaybackend.api.partyroom.domain.entity.data.history.PlaybackReactionHistoryData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Playback;
import com.pfplaybackend.api.partyroom.domain.enums.MessageTopic;
import com.pfplaybackend.api.partyroom.domain.enums.MotionType;
import com.pfplaybackend.api.partyroom.domain.service.PlaybackReactionDomainService;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.domain.value.PlaybackId;
import com.pfplaybackend.api.partyroom.event.RedisMessagePublisher;
import com.pfplaybackend.api.partyroom.event.message.AggregationMessage;
import com.pfplaybackend.api.partyroom.event.message.MotionMessage;
import com.pfplaybackend.api.partyroom.presentation.payload.request.ReactCurrentPlaybackRequest;
import com.pfplaybackend.api.partyroom.repository.history.PlaybackReactionHistoryRepository;
import com.pfplaybackend.api.user.domain.value.UserId;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaybackReactionService {
    // Using Repositories
    private final PlaybackReactionHistoryRepository playbackReactionHistoryRepository;
    // Using Domain Services
    private final PlaybackReactionDomainService playbackReactionDomainService;
    // Using Application Services
    private final DJManagementService djManagementService;
    private final PlaybackService playbackService;
    private final PartyroomInfoService partyroomInfoService;
    // Using RedisMessagePublisher
    private final RedisMessagePublisher redisMessagePublisher;
    // Using Proxy Services
    private final GrabMusicPeerService grabMusicService;
    private final UserActivityPeerService userActivityService;

    @Transactional
    public void reactToCurrentPlayback(PartyroomId partyroomId, ReactCurrentPlaybackRequest request) {
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        ActivePartyroomDto activePartyroom = partyroomInfoService.getMyActivePartyroom();
        // TODO [Check] activePartyroom.getId() == partyroomId
        PlaybackId currPlaybackId = activePartyroom.getCurrentPlaybackId();
        PlaybackReactionHistoryData historyData = findPrevHistoryData(currPlaybackId, partyContext.getUserId());
        boolean isExistHistory = historyData == null;
        if(isExistHistory) {
            return;
        }else {
            PlaybackReactionHistoryData newHistoryData = new PlaybackReactionHistoryData(partyContext.getUserId(), currPlaybackId);
            playbackReactionHistoryRepository.save(newHistoryData.applyLikedReaction());
            // Post Process...
            updateDjActivityScore(djManagementService.getDjByPlayback(currPlaybackId).getUserId(), 1);
            Playback updatedPlayback = updatePlaybackAggregation(currPlaybackId);
            publishAggregationChangedEvent(partyroomId, updatedPlayback);
            publishMotionChangedEvent(partyroomId);
        }
        // TODO 모션 변동 이벤트 발행 필요 여부 (웹소켓 토픽)
        // TODO 이펙트 변동 이벤트 발행 필요 여부 (웹소켓 토픽)
        // TODO 통계값 변동 이벤트 발행 여부 (웹소켓 토픽)
        // TODO Dj 점수 수정 처리 발생 여부 (피어 서비스)
        // TODO Grab 처리 발생 여부 (피어 서비스)
    }

    private PlaybackReactionHistoryData findPrevHistoryData(PlaybackId playbackId, UserId userId) {
        return playbackReactionHistoryRepository.findByPlaybackIdAndUserId(playbackId, userId);
    }

    private void publishMotionChangedEvent(PartyroomId partyroomId) {
        redisMessagePublisher.publish(MessageTopic.MOTION,
                new MotionMessage(partyroomId, MessageTopic.MOTION, MotionType.MOVE));
    }

    private void publishAggregationChangedEvent(PartyroomId partyroomId, Playback playback) {
        // TODO Get Aggregation of Current Playback ?
        // TODO Or Get Aggregation By Playback
        AggregationDto aggregationDto = new AggregationDto(playback.getLikeCount(), playback.getDislikeCount(), playback.getGrabCount());
        redisMessagePublisher.publish(MessageTopic.AGGREGATION,
                new AggregationMessage(partyroomId, MessageTopic.AGGREGATION, aggregationDto));
    }

    private void updateDjActivityScore(UserId djUserId, int score) {
        userActivityService.updateDjPointScore(djUserId, score);
    }

    private Playback updatePlaybackAggregation(PlaybackId playbackId) {
        return playbackService.updateLikeCount(playbackId);
    }

//    @Transactional
//    public void grabCurrentPlayback() {
//        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
//        /// 1. Reaction 이력 저장
//        // TODO Grabbed 토글로 동작해야 한다.
//        PlaybackReactionHistoryData historyData = new PlaybackReactionHistoryData();
//        playbackReactionHistory.save(historyData);
//        // 2. Reaction 행위 결과를 Dj 에게 점수 반영
//        // TODO (수정) Dj의 UserId 정보 조회
//        userActivityService.updateDjPointScore(new UserId(), 2);
//        // 3. 요청자의 Grab 플레이리스트에 현재 곡 정보를 저장
//        // TODO 현재 곡 정보 조회
//        grabMusicService.grabThisMusic();
//    }
}