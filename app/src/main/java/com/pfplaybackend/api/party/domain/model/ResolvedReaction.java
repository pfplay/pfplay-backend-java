package com.pfplaybackend.api.party.domain.model;

import com.pfplaybackend.api.party.domain.enums.MotionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResolvedReaction {
    MotionType motionType;
    int score;
}