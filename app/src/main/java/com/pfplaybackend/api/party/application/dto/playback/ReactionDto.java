package com.pfplaybackend.api.party.application.dto.playback;

import java.util.Map;

public record ReactionDto(
        Map<String, Boolean> history,
        AggregationDto aggregation
) {
    public static ReactionDto from(Map<String, Boolean> history, AggregationDto aggregation) {
        return new ReactionDto(history, aggregation);
    }
}
