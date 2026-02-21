package com.pfplaybackend.api.party.domain.model;

import com.pfplaybackend.api.party.domain.enums.MotionType;

import java.util.List;

public record ReactionPostProcessResult(
    boolean isAggregationChanged,
    boolean isMotionChanged,
    boolean isDjActivityScoreChanged,
    boolean isGrabStatusChanged,
    List<Integer> deltaRecord,
    int deltaScore,
    MotionType determinedMotionType
) {}
