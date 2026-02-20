package com.pfplaybackend.api.party.adapter.in.listener.message;

import com.pfplaybackend.api.common.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.domain.enums.MotionType;
import com.pfplaybackend.api.party.domain.enums.ReactionType;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;

import java.io.Serializable;

public record ReactionMotionMessage(
        PartyroomId partyroomId,
        MessageTopic eventType,
        ReactionType reactionType,
        MotionType motionType,
        CrewMotionInfo crew
) implements Serializable, GroupBroadcastMessage {

    public record CrewMotionInfo(long crewId) {}

    public static ReactionMotionMessage from(PartyroomId partyroomId, ReactionType reactionType, MotionType motionType, CrewId crewId) {
        return new ReactionMotionMessage(
                partyroomId,
                MessageTopic.REACTION_MOTION,
                reactionType,
                motionType,
                new CrewMotionInfo(crewId.getId())
        );
    }
}
