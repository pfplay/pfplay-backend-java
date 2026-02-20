package com.pfplaybackend.api.party.domain.event;

import com.pfplaybackend.api.common.domain.event.DomainEvent;
import com.pfplaybackend.api.party.domain.enums.MotionType;
import com.pfplaybackend.api.party.domain.enums.ReactionType;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import lombok.Getter;

@Getter
public class ReactionMotionChangedEvent extends DomainEvent {
    private final PartyroomId partyroomId;
    private final ReactionType reactionType;
    private final MotionType motionType;
    private final long crewId;

    public ReactionMotionChangedEvent(PartyroomId partyroomId, ReactionType reactionType,
                                       MotionType motionType, long crewId) {
        this.partyroomId = partyroomId;
        this.reactionType = reactionType;
        this.motionType = motionType;
        this.crewId = crewId;
    }
}
