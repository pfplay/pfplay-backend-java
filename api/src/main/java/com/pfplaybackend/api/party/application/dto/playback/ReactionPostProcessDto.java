package com.pfplaybackend.api.party.application.dto.playback;

import com.pfplaybackend.api.party.domain.enums.MotionType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
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
