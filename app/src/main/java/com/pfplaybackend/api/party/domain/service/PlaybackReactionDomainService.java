package com.pfplaybackend.api.party.domain.service;

import com.pfplaybackend.api.party.domain.model.ReactionPostProcessResult;
import com.pfplaybackend.api.party.domain.entity.data.history.PlaybackReactionHistoryData;
import com.pfplaybackend.api.party.domain.enums.MotionType;
import com.pfplaybackend.api.party.domain.enums.ReactionType;
import com.pfplaybackend.api.party.domain.model.ReactionState;
import com.pfplaybackend.api.party.domain.model.ReactionStateResolver;
import com.pfplaybackend.api.party.domain.model.ResolvedReaction;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaybackReactionDomainService {

    public ReactionState getReactionStateByHistory(PlaybackReactionHistoryData historyData) {
        return new ReactionState(historyData.isLiked(), historyData.isDisliked(), historyData.isGrabbed());
    }

    public ReactionState getTargetReactionState(ReactionState reactionState, ReactionType reactionType) {
        return ReactionStateResolver.getCombinedReactionState(reactionState, reactionType);
    }

    public ReactionPostProcessResult determinePostProcessing(ReactionState existingState, ReactionState targetState) {
        ResolvedReaction existingResolved = ReactionStateResolver.resolve(existingState);
        ResolvedReaction targetResolved = ReactionStateResolver.resolve(targetState);

        boolean isDifferentMotionType = diffMotionType(existingResolved, targetResolved);
        boolean isDifferentDjActivityScore = diffDjActivityScore(existingResolved, targetResolved);
        boolean isDifferentAggregation = diffAggregation(existingState, targetState);
        boolean isDifferentGrabStatus = diffGrabStatus(existingState, targetState);

        MotionType determinedMotionType = isDifferentMotionType ? targetResolved.getMotionType() : null;
        int deltaScore = isDifferentDjActivityScore ? targetResolved.getScore() - existingResolved.getScore() : 0;
        List<Integer> deltaRecord = isDifferentAggregation
                ? List.of(
                    convertBooleanToInt(targetState.isLiked()) - convertBooleanToInt(existingState.isLiked()),
                    convertBooleanToInt(targetState.isDisliked()) - convertBooleanToInt(existingState.isDisliked()),
                    convertBooleanToInt(targetState.isGrabbed()) - convertBooleanToInt(existingState.isGrabbed()))
                : null;

        return new ReactionPostProcessResult(
                isDifferentAggregation, isDifferentMotionType,
                isDifferentDjActivityScore, isDifferentGrabStatus,
                deltaRecord, deltaScore, determinedMotionType);
    }

    private boolean diffAggregation(ReactionState existingState, ReactionState targetState) {
        return !existingState.equals(targetState);
    }

    private boolean diffMotionType(ResolvedReaction existingResolved, ResolvedReaction targetResolved) {
        return existingResolved.getMotionType() != targetResolved.getMotionType();
    }

    private boolean diffDjActivityScore(ResolvedReaction existingResolved, ResolvedReaction targetResolved) {
        return existingResolved.getScore() != targetResolved.getScore();
    }

    private boolean diffGrabStatus(ReactionState existingState, ReactionState targetState) {
        return !existingState.isGrabbed() && targetState.isGrabbed();
    }

    private int convertBooleanToInt(boolean b) {
        return b ? 1 : 0;
    }
}
