package com.pfplaybackend.api.admin.application.service;

import com.pfplaybackend.api.admin.presentation.dto.response.SimulateReactionsResponse;
import com.pfplaybackend.api.party.application.dto.playback.ReactionPostProcessDto;
import com.pfplaybackend.api.party.application.peer.UserActivityPeerService;
import com.pfplaybackend.api.party.application.service.PlaybackInfoService;
import com.pfplaybackend.api.party.application.service.PlaybackReactionPostProcessService;
import com.pfplaybackend.api.party.domain.entity.data.history.PlaybackReactionHistoryData;
import com.pfplaybackend.api.party.domain.entity.domainmodel.Playback;
import com.pfplaybackend.api.party.domain.enums.ReactionType;
import com.pfplaybackend.api.party.domain.model.ReactionState;
import com.pfplaybackend.api.party.domain.service.PlaybackReactionDomainService;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.value.PlaybackId;
import com.pfplaybackend.api.party.infrastructure.repository.PlaybackReactionHistoryRepository;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for simulating reactions in separate transactions
 * Separated from AdminPartyroomService to ensure @Transactional works correctly
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReactionSimulationService {

    private final PlaybackInfoService playbackInfoService;
    private final PlaybackReactionDomainService playbackReactionDomainService;
    private final PlaybackReactionHistoryRepository playbackReactionHistoryRepository;
    private final PlaybackReactionPostProcessService playbackReactionPostProcessService;
    private final UserActivityPeerService userActivityPeerService;

    /**
     * Simulate a single reaction in a separate transaction
     * Admin version that doesn't rely on ThreadLocalContext
     *
     * @param userId User performing the reaction
     * @param crewId Crew ID
     * @param playbackId Current playback ID
     * @param partyroomId Partyroom ID
     * @param reactionType Type of reaction (LIKE/GRAB)
     * @param delayMs Delay in milliseconds before reaction
     * @return SimulatedReaction with delay information
     */
    @Transactional
    public SimulateReactionsResponse.SimulatedReaction simulateReaction(
            UserId userId,
            CrewId crewId,
            PlaybackId playbackId,
            PartyroomId partyroomId,
            ReactionType reactionType,
            int delayMs) {

        // 1. Get or create reaction history
        PlaybackReactionHistoryData historyData = playbackReactionHistoryRepository
                .findByPlaybackIdAndUserId(playbackId, userId)
                .orElse(new PlaybackReactionHistoryData(userId, playbackId));

        // 2. Get existing state
        ReactionState existingState = historyData.getId() != null
                ? playbackReactionDomainService.getReactionStateByHistory(historyData)
                : ReactionState.createBaseState();

        // 3. Calculate target state
        ReactionState targetState = playbackReactionDomainService.getTargetReactionState(existingState, reactionType);

        // 4. Save reaction history
        playbackReactionHistoryRepository.save(historyData.applyReactionState(targetState));

        // 5. Determine post-processing
        ReactionPostProcessDto postProcessDto = playbackReactionDomainService
                .determinePostProcessing(existingState, targetState);

        // 6. Execute post-processing manually (avoid ThreadLocalContext dependency)
        Playback playback = playbackInfoService.getPlaybackById(playbackId);

        // Note: Skip actual grab music operation for simulation
        // Only events and aggregation will be updated

        // Update DJ activity score
        if (postProcessDto.isDjActivityScoreChanged()) {
            userActivityPeerService.updateDjPointScore(playback.getUserId(), postProcessDto.getDeltaScore());
        }

        // Update playback aggregation (includes GRAB count)
        if (postProcessDto.isAggregationChanged()) {
            playbackReactionPostProcessService.updatePlaybackAggregation(playback, postProcessDto.getDeltaRecord());
            playbackReactionPostProcessService.publishAggregationChangedEvent(partyroomId, playback);
        }

        // Publish motion event (includes GRAB motion)
        playbackReactionPostProcessService.publishMotionChangedEvent(
                partyroomId,
                reactionType,
                postProcessDto.getDeterminedMotionType(),
                crewId
        );

        // 7. Return simulated reaction result
        log.debug("Simulated {} reaction after {}ms delay: userId={}, playbackId={}",
                reactionType, delayMs, userId.getUid(), playbackId.getId());

        return SimulateReactionsResponse.SimulatedReaction.builder()
                .userId(userId.getUid().toString())
                .reactionType(reactionType.name())
                .eventPublished(true)
                .build();
    }
}
