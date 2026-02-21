package com.pfplaybackend.api.admin.adapter.in.web.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Response DTO for reaction simulation
 */
@Getter
@Builder
@AllArgsConstructor
public class SimulateReactionsResponse {

    /**
     * Partyroom ID where reactions occurred
     */
    private Long partyroomId;

    /**
     * Playback ID that received reactions
     */
    private Long playbackId;

    /**
     * List of simulated reactions
     */
    private List<SimulatedReaction> reactions;

    /**
     * Updated aggregation counts
     */
    private AggregationCounts aggregation;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class SimulatedReaction {
        private String userId;
        private String reactionType;  // LIKE or GRAB
        private Boolean eventPublished;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class AggregationCounts {
        private Integer likeCount;
        private Integer dislikeCount;
        private Integer grabCount;
    }
}
