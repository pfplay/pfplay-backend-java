package com.pfplaybackend.api.partyview.application.dto;

import com.pfplaybackend.api.party.application.dto.playback.AggregationDto;
import com.pfplaybackend.api.party.application.dto.playback.ReactionHistoryDto;

public record ReactionDto(
        ReactionHistoryDto history,
        AggregationDto aggregation
) {
    public static ReactionDto from(ReactionHistoryDto history, AggregationDto aggregation) {
        return new ReactionDto(history, aggregation);
    }
}
