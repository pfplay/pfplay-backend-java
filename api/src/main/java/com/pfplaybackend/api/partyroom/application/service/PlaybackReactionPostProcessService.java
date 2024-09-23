package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.partyroom.application.aspect.context.PartyContext;
import com.pfplaybackend.api.partyroom.application.dto.AggregationDto;
import com.pfplaybackend.api.partyroom.application.dto.ReactionPostProcessDto;
import com.pfplaybackend.api.partyroom.application.peer.GrabMusicPeerService;
import com.pfplaybackend.api.partyroom.application.peer.UserActivityPeerService;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Playback;
import com.pfplaybackend.api.partyroom.event.MessageTopic;
import com.pfplaybackend.api.partyroom.domain.enums.MotionType;
import com.pfplaybackend.api.partyroom.domain.value.CrewId;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.domain.value.PlaybackId;
import com.pfplaybackend.api.config.redis.RedisMessagePublisher;
import com.pfplaybackend.api.partyroom.event.message.ReactionAggregationMessage;
import com.pfplaybackend.api.partyroom.event.message.ReactionMotionMessage;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaybackReactionPostProcessService {
    private final PlaybackInfoService playbackInfoService;
    // Using RedisMessagePublisher
    private final RedisMessagePublisher messagePublisher;
    // Using Proxy Services
    private final GrabMusicPeerService grabMusicService;
    private final UserActivityPeerService userActivityService;

    public void postProcess(ReactionPostProcessDto postProcessDto, PartyroomId partyroomId, PlaybackId playbackId, CrewId crewId) {
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        Playback playback = playbackInfoService.getPlaybackById(playbackId);
        if(postProcessDto.isGrabStatusChanged()) {
            grabMusic(partyContext.getUserId(), playback);
        }
        if(postProcessDto.isDjActivityScoreChanged()) {
            updateDjActivityScore(playback.getUserId(), postProcessDto.getDeltaScore());
        }
        if(postProcessDto.isAggregationChanged()) {
            updatePlaybackAggregation(playback, postProcessDto.getDeltaRecord());
            publishAggregationChangedEvent(partyroomId, playback);
        }
        if(postProcessDto.isMotionChanged()) {
            publishMotionChangedEvent(partyroomId, postProcessDto.getDeterminedMotionType(), crewId);
        }
    }

    // PostProcess After Playback Reaction
    public void publishMotionChangedEvent(PartyroomId partyroomId, MotionType motionType, CrewId crewId) {
        ReactionMotionMessage reactionMotionMessage = ReactionMotionMessage.from(partyroomId, motionType, crewId);
        messagePublisher.publish(MessageTopic.REACTION_MOTION, reactionMotionMessage);
    }

    public void updateDjActivityScore(UserId djUserId, int deltaScore) {
        userActivityService.updateDjPointScore(djUserId, deltaScore);
    }

    public void updatePlaybackAggregation(Playback playback, List<Integer> deltaRecord) {
        playbackInfoService.updatePlaybackAggregation(playback, deltaRecord);
    }

    public void publishAggregationChangedEvent(PartyroomId partyroomId, Playback playback) {
        AggregationDto aggregationDto = new AggregationDto(playback.getLikeCount(), playback.getDislikeCount(), playback.getGrabCount());
        messagePublisher.publish(MessageTopic.REACTION_AGGREGATION,
                new ReactionAggregationMessage(partyroomId, MessageTopic.REACTION_AGGREGATION, aggregationDto));
    }

    public void grabMusic(UserId userId, Playback playback) {
        // TODO 이미 동일한 LinkId를 보유하고 있다면 예외 발생
        grabMusicService.grabMusic(userId, playback.getLinkId());
    }
}
