package com.pfplaybackend.api.party.domain.model;

import com.pfplaybackend.api.party.domain.enums.MotionType;

import java.util.List;

public record ReactionPostProcessResult(
    boolean aggregationChanged,
    boolean motionChanged,
    boolean djActivityScoreChanged,
    boolean grabStatusChanged,
    List<Integer> deltaRecord,
    int deltaScore,
    MotionType determinedMotionType
) {}
