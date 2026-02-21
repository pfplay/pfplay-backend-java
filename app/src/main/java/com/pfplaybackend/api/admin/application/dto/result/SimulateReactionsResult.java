package com.pfplaybackend.api.admin.application.dto.result;

import java.util.List;

public record SimulateReactionsResult(
        Long partyroomId,
        Long playbackId,
        List<SimulatedReaction> reactions,
        AggregationCounts aggregation
) {
    public record SimulatedReaction(String userId, String reactionType, Boolean eventPublished) {}

    public record AggregationCounts(Integer likeCount, Integer dislikeCount, Integer grabCount) {}
}
