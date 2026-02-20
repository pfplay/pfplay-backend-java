package com.pfplaybackend.api.party.domain.service;

import com.pfplaybackend.api.party.domain.model.ReactionPostProcessResult;
import com.pfplaybackend.api.party.domain.entity.data.history.PlaybackReactionHistoryData;
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
        ReactionPostProcessResult reactionPostProcessDto = new ReactionPostProcessResult();

        ResolvedReaction existingResolved = ReactionStateResolver.resolve(existingState);
        ResolvedReaction targetResolved = ReactionStateResolver.resolve(targetState);

        boolean isDifferentMotionType = diffMotionType(existingResolved, targetResolved);
        boolean isDifferentDjActivityScore = diffDjActivityScore(existingResolved, targetResolved);
        boolean isDifferentAggregation =  diffAggregation(existingState, targetState);
        boolean isDifferentGrabStatus = diffGrabStatus(existingState, targetState);

        reactionPostProcessDto.setMotionChanged(isDifferentMotionType);
        reactionPostProcessDto.setDjActivityScoreChanged(isDifferentDjActivityScore);
        reactionPostProcessDto.setAggregationChanged(isDifferentAggregation);
        reactionPostProcessDto.setGrabStatusChanged(isDifferentGrabStatus);

        if(isDifferentMotionType) {
            reactionPostProcessDto.setDeterminedMotionType(targetResolved.getMotionType());
        }

        if(isDifferentDjActivityScore) {
            int deltaDjScore = targetResolved.getScore() - existingResolved.getScore();
            reactionPostProcessDto.setDeltaScore(deltaDjScore);
        }

        if(isDifferentAggregation) {
            int likeCountDelta = convertBooleanToInt(targetState.isLiked()) - convertBooleanToInt(existingState.isLiked());
            int dislikeCountDelta = convertBooleanToInt(targetState.isDisliked()) - convertBooleanToInt(existingState.isDisliked());
            int grabCountDelta = convertBooleanToInt(targetState.isGrabbed()) - convertBooleanToInt(existingState.isGrabbed());
            reactionPostProcessDto.setDeltaRecord(List.of(likeCountDelta, dislikeCountDelta, grabCountDelta));
        }
        return reactionPostProcessDto;
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
