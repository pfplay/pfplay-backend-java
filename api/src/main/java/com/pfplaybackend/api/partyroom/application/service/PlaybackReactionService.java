package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.partyroom.application.aspect.context.PartyContext;
import com.pfplaybackend.api.partyroom.application.dto.ActivePartyroomDto;
import com.pfplaybackend.api.partyroom.application.dto.ReactionPostProcessDto;
import com.pfplaybackend.api.partyroom.domain.entity.data.history.PlaybackReactionHistoryData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Crew;
import com.pfplaybackend.api.partyroom.domain.enums.ReactionType;
import com.pfplaybackend.api.partyroom.domain.model.ReactionState;
import com.pfplaybackend.api.partyroom.domain.service.PlaybackReactionDomainService;
import com.pfplaybackend.api.partyroom.domain.value.CrewId;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.domain.value.PlaybackId;
import com.pfplaybackend.api.partyroom.repository.history.PlaybackReactionHistoryRepository;
import com.pfplaybackend.api.user.domain.value.UserId;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlaybackReactionService {
    
    private final PlaybackReactionHistoryRepository playbackReactionHistoryRepository;
    private final PlaybackReactionDomainService playbackReactionDomainService;
    private final PartyroomInfoService partyroomInfoService;
    private final PlaybackReactionPostProcessService playbackReactionPostProcessService;

    @Transactional
    public void reactToCurrentPlayback(PartyroomId partyroomId, ReactionType reactionType) {
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
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
        playbackReactionPostProcessService.postProcess(reactionPostProcessDto, partyroomId, playbackId, new CrewId(crew.getId()));

        // TODO
//        System.out.println(targetState.isLiked());
//        System.out.println(targetState.isDisliked());
//        System.out.println(targetState.isGrabbed());
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