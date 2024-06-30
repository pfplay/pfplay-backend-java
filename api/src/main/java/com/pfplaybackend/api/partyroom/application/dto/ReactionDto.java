package com.pfplaybackend.api.partyroom.application.dto;

import java.util.List;
import java.util.Map;

public class ReactionDto {
    private Map<String, Boolean> pushed;
    private AggregationDto aggregation;
    private List<Long> motionIds;
}