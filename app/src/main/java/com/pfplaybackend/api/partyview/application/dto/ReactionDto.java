package com.pfplaybackend.api.partyview.application.dto;

import com.pfplaybackend.api.party.application.dto.playback.AggregationDto;

import java.util.Map;

public record ReactionDto(
        Map<String, Boolean> history,
        AggregationDto aggregation
) {
    public static ReactionDto from(Map<String, Boolean> history, AggregationDto aggregation) {
        return new ReactionDto(history, aggregation);
    }
}
