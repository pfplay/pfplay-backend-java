package com.pfplaybackend.api.partyroom.application.dto.playback;

import com.pfplaybackend.api.partyroom.domain.enums.MotionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReactionPostProcessDto {
    private boolean isAggregationChanged;
    private boolean isMotionChanged;
    private boolean isDjActivityScoreChanged;
    private boolean isGrabStatusChanged;
    //
    private List<Integer> deltaRecord;
    private int deltaScore;
    private MotionType determinedMotionType;
}
