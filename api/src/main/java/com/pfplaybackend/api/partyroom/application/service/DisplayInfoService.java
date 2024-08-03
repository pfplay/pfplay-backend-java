package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.partyroom.application.aspect.context.PartyContext;
import com.pfplaybackend.api.partyroom.application.dto.*;
import com.pfplaybackend.api.partyroom.domain.entity.data.history.PlaybackReactionHistoryData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Playback;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.domain.value.PlaybackId;
import com.pfplaybackend.api.partyroom.repository.PlaybackRepository;
import com.pfplaybackend.api.partyroom.repository.history.PlaybackReactionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DisplayInfoService {

    private final PartyroomInfoService partyroomInfoService;
    private final PlaybackInfoService playbackInfoService;
    private final PlaybackReactionService playbackReactionService;

    public DisplayDto getDisplayInfo() {
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        ActivePartyroomDto activePartyroom = partyroomInfoService.getMyActivePartyroom().orElseThrow();
        boolean isPlaybackActivated = activePartyroom.isPlaybackActivated();
        Playback playback = playbackInfoService.getPlaybackById(activePartyroom.getCurrentPlaybackId());

        Optional<PlaybackReactionHistoryData> optional = playbackReactionService.findPrevHistoryData(new PlaybackId(playback.getId()), partyContext.getUserId());
        Map<String, Boolean> history = new HashMap<>();
        if(optional.isPresent()) {
            PlaybackReactionHistoryData playbackReactionHistoryData = optional.get();
            history.put("isLiked", playbackReactionHistoryData.isLiked());
            history.put("isDislike", playbackReactionHistoryData.isDisliked());
            history.put("isGrabbed", playbackReactionHistoryData.isGrabbed());
        }else {
            history.put("isLiked", false);
            history.put("isDislike", false);
            history.put("isGrabbed", false);
        }

        AggregationDto aggregationDto = new AggregationDto(playback.getLikeCount(), playback.getDislikeCount(), playback.getGrabCount());
        ReactionDto reactionDto = ReactionDto.from(history, aggregationDto);

        PlaybackDto playbackDto = null;
        if(isPlaybackActivated) {
            playbackDto = new PlaybackDto(
                    playback.getId(),
                    playback.getLinkId(),
                    playback.getName(),
                    playback.getDuration(),
                    playback.getThumbnailImage(),
                    playback.getEndTime()
            );
        }

        // 1.1. ReactionState existingState = playbackReactionDomainService.getReactionStateByHistory(historyData);
        // 1.2. ReactionState existingState = ReactionState.createBaseState();
        // 2.   ResolvedReaction existingResolved = ReactionStateResolver.resolve(existingState);
        return new DisplayDto(isPlaybackActivated, playbackDto, reactionDto);
    }
}
