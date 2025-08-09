package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.party.application.aspect.context.PartyContext;
import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomDto;
import com.pfplaybackend.api.party.application.dto.playback.ReactionPostProcessDto;
import com.pfplaybackend.api.party.domain.entity.data.history.PlaybackReactionHistoryData;
import com.pfplaybackend.api.party.domain.entity.domainmodel.Crew;
import com.pfplaybackend.api.party.domain.enums.ReactionType;
import com.pfplaybackend.api.party.domain.exception.ReactionException;
import com.pfplaybackend.api.party.domain.model.ReactionState;
import com.pfplaybackend.api.party.domain.service.PlaybackReactionDomainService;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.value.PlaybackId;
import com.pfplaybackend.api.party.infrastructure.repository.PlaybackReactionHistoryRepository;
import com.pfplaybackend.api.user.domain.value.UserId;
import jakarta.transaction.Transactional;
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
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        // TODO GT → GRAB 불가능하게 만들기
        if(AuthorityTier.GT.equals(partyContext.getAuthorityTier()) && reactionType.equals(ReactionType.GRAB)) {
            throw ExceptionCreator.create(ReactionException.INVALID_REACTION);
        }

        ActivePartyroomDto myActivePartyroom = partyroomInfoService.getMyActivePartyroom().orElseThrow();
        // TODO [Check] MyActivePartyroom.getId() == partyroomId
        PlaybackId playbackId = myActivePartyroom.getCurrentPlaybackId();
        // Find whether existing history exists
        PlaybackReactionHistoryData historyData = getValidReactionHistoryData(partyContext, playbackId);
        ReactionState existingState = getExistingState(historyData);
        ReactionState targetState = getTargetState(existingState, reactionType);

        ReactionPostProcessDto reactionPostProcessDto = executeProcess(historyData, existingState, targetState);
        // Get CrewId for Event Propagation
        Optional<Crew> optional  = partyroomInfoService.getCrewByUserId(partyroomId, partyContext.getUserId());
        Crew crew = optional.orElseThrow();
        playbackReactionPostProcessService.postProcess(reactionPostProcessDto, reactionType, partyroomId, playbackId, new CrewId(crew.getId()));
        return Map.of(
            "isLiked", targetState.isLiked(),
            "isDisliked", targetState.isDisliked(),
            "isGrabbed", targetState.isGrabbed()
        );
    }

    private PlaybackReactionHistoryData getValidReactionHistoryData(PartyContext partyContext, PlaybackId playbackId) {
        Optional<PlaybackReactionHistoryData> optional = findPrevHistoryData(playbackId, partyContext.getUserId());
        if(optional.isPresent()) {
            return optional.orElseThrow();
        }else {
            return new PlaybackReactionHistoryData(partyContext.getUserId(), playbackId);
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

    // FIXME Change Method Name
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