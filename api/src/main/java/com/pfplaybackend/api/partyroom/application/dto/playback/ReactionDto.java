package com.pfplaybackend.api.partyroom.application.dto.playback;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class ReactionDto {
    private Map<String, Boolean> history;
    private AggregationDto aggregation;
    // private Map<String, List<Long>> motion;

    public static ReactionDto from(Map<String, Boolean> history, AggregationDto aggregation) {
        return new ReactionDto(history, aggregation);
    }
}