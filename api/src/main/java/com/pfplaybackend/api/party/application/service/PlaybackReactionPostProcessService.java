package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.party.application.dto.playback.ReactionPostProcessDto;
import com.pfplaybackend.api.party.application.port.out.PlaylistCommandPort;
import com.pfplaybackend.api.party.application.port.out.UserActivityPort;
import com.pfplaybackend.api.party.domain.entity.data.PlaybackData;
import com.pfplaybackend.api.party.domain.enums.MotionType;
import com.pfplaybackend.api.party.domain.enums.ReactionType;
import com.pfplaybackend.api.party.domain.event.ReactionAggregationChangedEvent;
import com.pfplaybackend.api.party.domain.event.ReactionMotionChangedEvent;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.value.PlaybackId;
import com.pfplaybackend.api.common.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaybackReactionPostProcessService {
    private final PlaybackInfoService playbackInfoService;
    private final ApplicationEventPublisher eventPublisher;
    private final PlaylistCommandPort grabMusicService;
    private final UserActivityPort userActivityService;

    public void postProcess(ReactionPostProcessDto postProcessDto, ReactionType reactionType, PartyroomId partyroomId, PlaybackId playbackId, CrewId crewId) {
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
        PlaybackData playback = playbackInfoService.getPlaybackById(playbackId);
        if(postProcessDto.isGrabStatusChanged()) {
            grabMusic(authContext.getUserId(), playback);
        }
        if(postProcessDto.isDjActivityScoreChanged()) {
            updateDjActivityScore(playback.getUserId(), postProcessDto.getDeltaScore());
        }
        if(postProcessDto.isAggregationChanged()) {
            updatePlaybackAggregation(playback, postProcessDto.getDeltaRecord());
            publishAggregationChangedEvent(partyroomId, playback);
        }
        publishMotionChangedEvent(partyroomId, reactionType, postProcessDto.getDeterminedMotionType(), crewId);
    }

    public void publishMotionChangedEvent(PartyroomId partyroomId, ReactionType reactionType, MotionType motionType, CrewId crewId) {
        eventPublisher.publishEvent(new ReactionMotionChangedEvent(partyroomId, reactionType, motionType, crewId.getId()));
    }

    public void updateDjActivityScore(UserId djUserId, int deltaScore) {
        userActivityService.updateDjPointScore(djUserId, deltaScore);
    }

    public void updatePlaybackAggregation(PlaybackData playback, List<Integer> deltaRecord) {
        playbackInfoService.updatePlaybackAggregation(playback, deltaRecord);
    }

    public void publishAggregationChangedEvent(PartyroomId partyroomId, PlaybackData playback) {
        eventPublisher.publishEvent(new ReactionAggregationChangedEvent(
                partyroomId, playback.getLikeCount(), playback.getDislikeCount(), playback.getGrabCount()));
    }

    public void grabMusic(UserId userId, PlaybackData playback) {
        // TODO 이미 동일한 LinkId를 보유하고 있다면 예외 발생
        grabMusicService.grabMusic(userId, playback.getLinkId());
    }
}
