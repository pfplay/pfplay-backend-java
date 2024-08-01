package com.pfplaybackend.api.partyroom.domain.model;

import com.pfplaybackend.api.partyroom.domain.enums.MotionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResolvedReaction {
    MotionType motionType;
    int score;
}