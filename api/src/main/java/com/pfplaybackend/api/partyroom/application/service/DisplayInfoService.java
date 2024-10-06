package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.partyroom.application.aspect.context.PartyContext;
import com.pfplaybackend.api.partyroom.application.dto.partyroom.ActivePartyroomDto;
import com.pfplaybackend.api.partyroom.application.dto.dj.CurrentDjDto;
import com.pfplaybackend.api.partyroom.application.dto.playback.AggregationDto;
import com.pfplaybackend.api.partyroom.application.dto.playback.DisplayDto;
import com.pfplaybackend.api.partyroom.application.dto.playback.PlaybackDto;
import com.pfplaybackend.api.partyroom.application.dto.playback.ReactionDto;
import com.pfplaybackend.api.partyroom.domain.entity.data.history.PlaybackReactionHistoryData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Crew;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Playback;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.domain.value.PlaybackId;
import com.pfplaybackend.api.partyroom.exception.CrewException;
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
        Optional<ActivePartyroomDto> optActivePartyroom = partyroomInfoService.getMyActivePartyroom();
        // Throw Exception
        if(optActivePartyroom.isEmpty()) throw ExceptionCreator.create(CrewException.NOT_FOUND_ACTIVE_ROOM);

        ActivePartyroomDto activePartyroom = optActivePartyroom.get();
        PartyroomId partyroomId = new PartyroomId(activePartyroom.getId());
        boolean isPlaybackActivated = activePartyroom.isPlaybackActivated();

        if(isPlaybackActivated) {
            Playback playback = playbackInfoService.getPlaybackById(activePartyroom.getCurrentPlaybackId());
            Crew djInfo = getCurrentDjInfo(partyroomId, playback);
            CurrentDjDto currentDjDto = new CurrentDjDto(djInfo.getId());
            //
            Optional<PlaybackReactionHistoryData> optional = playbackReactionService.findPrevHistoryData(new PlaybackId(playback.getId()), partyContext.getUserId());
            AggregationDto aggregationDto = new AggregationDto(playback.getLikeCount(), playback.getDislikeCount(), playback.getGrabCount());
            ReactionDto reactionDto = ReactionDto.from(getHistory(optional), aggregationDto);
            PlaybackDto playbackDto = new PlaybackDto(playback.getId(), playback.getLinkId(), playback.getName(), playback.getDuration(), playback.getThumbnailImage(), playback.getEndTime());
            return new DisplayDto(true, playbackDto, reactionDto, currentDjDto);
        }else {
            return new DisplayDto(false, null, null, null);
        }
        // 1.1. ReactionState existingState = playbackReactionDomainService.getReactionStateByHistory(historyData);
        // 1.2. ReactionState existingState = ReactionState.createBaseState();
        // 2.   ResolvedReaction existingResolved = ReactionStateResolver.resolve(existingState);
    }

    private Crew getCurrentDjInfo(PartyroomId partyroomId, Playback playback) {
        // FIXME Dj 대기열에서의 Dj 아이템
        // FIXME Crew 목록에서의 아이템
        // crewSet 에서 조회하기 때문에 처리는 정상적으로 된다.
        // 하지만 djSet 에서는 존재하지 않는다.
        return partyroomInfoService.getCrewByUserId(partyroomId, playback.getUserId()).orElseThrow();
    }

    private static Map<String, Boolean> getHistory(Optional<PlaybackReactionHistoryData> optional) {
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
        return history;
    }
}
