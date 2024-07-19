package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.partyroom.application.dto.AggregationDto;
import com.pfplaybackend.api.partyroom.application.dto.ReactionPostProcessDto;
import com.pfplaybackend.api.partyroom.application.peer.GrabMusicPeerService;
import com.pfplaybackend.api.partyroom.application.peer.UserActivityPeerService;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Playback;
import com.pfplaybackend.api.partyroom.domain.enums.MessageTopic;
import com.pfplaybackend.api.partyroom.domain.enums.MotionType;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.domain.value.PlaybackId;
import com.pfplaybackend.api.partyroom.event.RedisMessagePublisher;
import com.pfplaybackend.api.partyroom.event.message.AggregationMessage;
import com.pfplaybackend.api.partyroom.event.message.MotionMessage;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaybackReactionPostProcessService {
    private final PlaybackService playbackService;
    // Using RedisMessagePublisher
    private final RedisMessagePublisher redisMessagePublisher;
    // Using Proxy Services
    private final GrabMusicPeerService grabMusicService;
    private final UserActivityPeerService userActivityService;

    public void postProcess(ReactionPostProcessDto postProcessDto, PartyroomId partyroomId, PlaybackId playbackId) {
        // TODO Get Playback Object By PlaybackId
        Playback playback = new Playback();

        if(postProcessDto.isMotionChanged()) {
            publishMotionChangedEvent(partyroomId, postProcessDto.getDeterminedMotionType());
        }
        if(postProcessDto.isDjActivityScoreChanged()) {
            UserId djUserId = new UserId();
            updateDjActivityScore(djUserId, postProcessDto.getDeltaScore());
        }
        if(postProcessDto.isAggregationChanged()) {
            // TODO Get Playback's Count information
            updatePlaybackAggregation(playback);
            publishAggregationChangedEvent(partyroomId, playback);
        }
        if(postProcessDto.isGrabStatusChanged()) {
            grabMusic(playback);
        }
    }

    // PostProcess After Playback Reaction
    public void publishMotionChangedEvent(PartyroomId partyroomId, MotionType motionType) {
        redisMessagePublisher.publish(MessageTopic.MOTION,
                new MotionMessage(partyroomId, MessageTopic.MOTION, motionType));
    }

    public void updateDjActivityScore(UserId djUserId, int deltaScore) {
        userActivityService.updateDjPointScore(djUserId, deltaScore);
    }

    public void updatePlaybackAggregation(Playback playback) {
        playbackService.updateAggregation(playback);
    }

    public void publishAggregationChangedEvent(PartyroomId partyroomId, Playback playback) {
        AggregationDto aggregationDto = new AggregationDto(playback.getLikeCount(), playback.getDislikeCount(), playback.getGrabCount());
        redisMessagePublisher.publish(MessageTopic.AGGREGATION,
                new AggregationMessage(partyroomId, MessageTopic.AGGREGATION, aggregationDto));
    }

    public void grabMusic(Playback playback) {
        // TODO Need Music's linkId
        grabMusicService.grabMusic(playback.getLinkId());
    }
}
