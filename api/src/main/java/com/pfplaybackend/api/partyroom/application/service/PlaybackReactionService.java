package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.partyroom.application.aspect.context.PartyContext;
import com.pfplaybackend.api.partyroom.application.dto.ActivePartyroomDto;
import com.pfplaybackend.api.partyroom.application.dto.AggregationDto;
import com.pfplaybackend.api.partyroom.application.dto.ReactionPostProcessDto;
import com.pfplaybackend.api.partyroom.application.peer.GrabMusicPeerService;
import com.pfplaybackend.api.partyroom.application.peer.UserActivityPeerService;
import com.pfplaybackend.api.partyroom.domain.entity.data.history.PlaybackReactionHistoryData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Playback;
import com.pfplaybackend.api.partyroom.domain.enums.MessageTopic;
import com.pfplaybackend.api.partyroom.domain.enums.MotionType;
import com.pfplaybackend.api.partyroom.domain.enums.ReactionType;
import com.pfplaybackend.api.partyroom.domain.model.ReactionState;
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

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlaybackReactionService {
    // Using Repositories
    private final PlaybackReactionHistoryRepository playbackReactionHistoryRepository;
    // Using Domain Services
    private final PlaybackReactionDomainService playbackReactionDomainService;
    // Using Application Services
    private final PartyroomInfoService partyroomInfoService;
    private final PlaybackReactionPostProcessService playbackReactionPostProcessService;

    @Transactional
    public void reactToCurrentPlayback(PartyroomId partyroomId, ReactionType reactionType) {
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        ActivePartyroomDto myActivePartyroom = partyroomInfoService.getMyActivePartyroom();
        // TODO [Check] MyActivePartyroom.getId() == partyroomId
        PlaybackId playbackId = myActivePartyroom.getCurrentPlaybackId();
        // Find whether existing history exists
        ReactionPostProcessDto reactionPostProcessDto = getReactionPostProcess(partyContext, playbackId, reactionType);
        playbackReactionPostProcessService.postProcess(reactionPostProcessDto, partyroomId, playbackId);
    }

    // FIXME Change Method Name
    private ReactionPostProcessDto getReactionPostProcess(PartyContext partyContext, PlaybackId playbackId, ReactionType reactionType) {
        Optional<PlaybackReactionHistoryData> optional = findPrevHistoryData(playbackId, partyContext.getUserId());
        if(optional.isPresent()) {
            PlaybackReactionHistoryData historyData = optional.orElseThrow();
            ReactionState existingState = playbackReactionDomainService.getReactionStateByHistory(historyData);
            return executeProcess(historyData, existingState, reactionType);
        }else {
            PlaybackReactionHistoryData newHistoryData = new PlaybackReactionHistoryData(partyContext.getUserId(), playbackId);
            ReactionState existingState = ReactionState.createBaseState();
            return executeProcess(newHistoryData, existingState, reactionType);
        }
    }

    // FIXME Change Method Name
    private ReactionPostProcessDto executeProcess(PlaybackReactionHistoryData historyData, ReactionState existingState, ReactionType reactionType) {
        // Calculate to get TargetState
        ReactionState targetState = playbackReactionDomainService.getTargetReactionState(existingState, reactionType);
        // Determine 'ReactionPostProcessDto' by diff existingState with targetState
        // Save(Update) History Record
        playbackReactionHistoryRepository.save(historyData.applyReactionState(targetState));
        // Delegate Post Processor
        return playbackReactionDomainService.determinePostProcessing(existingState, targetState);
    }

    private Optional<PlaybackReactionHistoryData> findPrevHistoryData(PlaybackId playbackId, UserId userId) {
        return playbackReactionHistoryRepository.findByPlaybackIdAndUserId(playbackId, userId);
    }
}