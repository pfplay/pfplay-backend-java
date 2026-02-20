package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomDto;
import com.pfplaybackend.api.party.application.dto.playback.ReactionPostProcessDto;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.entity.data.history.PlaybackReactionHistoryData;
import com.pfplaybackend.api.party.domain.enums.ReactionType;
import com.pfplaybackend.api.party.domain.exception.ReactionException;
import com.pfplaybackend.api.party.domain.model.ReactionState;
import com.pfplaybackend.api.party.domain.service.PlaybackReactionDomainService;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.value.PlaybackId;
import com.pfplaybackend.api.party.adapter.out.persistence.PlaybackReactionHistoryRepository;
import com.pfplaybackend.api.common.domain.value.UserId;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlaybackReactionService {

    private final PlaybackReactionHistoryRepository playbackReactionHistoryRepository;
    private final PlaybackReactionDomainService playbackReactionDomainService;
    private final PartyroomInfoService partyroomInfoService;
    private final PlaybackReactionPostProcessService playbackReactionPostProcessService;

    @Transactional
    public Map<String, Boolean> reactToCurrentPlayback(PartyroomId partyroomId, ReactionType reactionType) {
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
        if(AuthorityTier.GT.equals(authContext.getAuthorityTier()) && reactionType.equals(ReactionType.GRAB)) {
            throw ExceptionCreator.create(ReactionException.INVALID_REACTION);
        }

        ActivePartyroomDto myActivePartyroom = partyroomInfoService.getMyActivePartyroom().orElseThrow();
        PlaybackId playbackId = myActivePartyroom.getCurrentPlaybackId();
        PlaybackReactionHistoryData historyData = getValidReactionHistoryData(authContext, playbackId);
        ReactionState existingState = getExistingState(historyData);
        ReactionState targetState = getTargetState(existingState, reactionType);

        ReactionPostProcessDto reactionPostProcessDto = executeProcess(historyData, existingState, targetState);
        Optional<CrewData> optional = partyroomInfoService.getCrewByUserId(partyroomId, authContext.getUserId());
        CrewData crew = optional.orElseThrow();
        playbackReactionPostProcessService.postProcess(reactionPostProcessDto, reactionType, partyroomId, playbackId, new CrewId(crew.getId()));
        return Map.of(
            "isLiked", targetState.isLiked(),
            "isDisliked", targetState.isDisliked(),
            "isGrabbed", targetState.isGrabbed()
        );
    }

    private PlaybackReactionHistoryData getValidReactionHistoryData(AuthContext authContext, PlaybackId playbackId) {
        Optional<PlaybackReactionHistoryData> optional = findPrevHistoryData(playbackId, authContext.getUserId());
        if(optional.isPresent()) {
            return optional.orElseThrow();
        }else {
            return new PlaybackReactionHistoryData(authContext.getUserId(), playbackId);
        }
    }

    private ReactionState getExistingState(PlaybackReactionHistoryData historyData) {
        Optional<Long> optional = Optional.ofNullable(historyData.getId());
        if(optional.isPresent()) {
            return playbackReactionDomainService.getReactionStateByHistory(historyData);
        }else {
            return ReactionState.createBaseState();
        }
    }

    private ReactionState getTargetState(ReactionState existingState, ReactionType reactionType) {
        return playbackReactionDomainService.getTargetReactionState(existingState, reactionType);
    }

    private ReactionPostProcessDto executeProcess(PlaybackReactionHistoryData historyData,
                                                  ReactionState existingState,
                                                  ReactionState targetState) {
        playbackReactionHistoryRepository.save(historyData.applyReactionState(targetState));
        return playbackReactionDomainService.determinePostProcessing(existingState, targetState);
    }

    public Optional<PlaybackReactionHistoryData> findPrevHistoryData(PlaybackId playbackId, UserId userId) {
        return playbackReactionHistoryRepository.findByPlaybackIdAndUserId(playbackId, userId);
    }
}
