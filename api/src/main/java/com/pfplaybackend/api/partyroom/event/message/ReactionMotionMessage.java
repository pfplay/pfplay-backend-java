package com.pfplaybackend.api.partyroom.event.message;

import com.pfplaybackend.api.partyroom.event.MessageTopic;
import com.pfplaybackend.api.partyroom.domain.enums.MotionType;
import com.pfplaybackend.api.partyroom.domain.value.CrewId;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReactionMotionMessage implements Serializable {
    private PartyroomId partyroomId;
    private MessageTopic eventType;
    private MotionType motionType;
    private Map<String, Object> crew;

    public static ReactionMotionMessage from(PartyroomId partyroomId, MotionType motionType, CrewId crewId) {
        Map<String, Object> crew = new HashMap<>();
        crew.put("crewId", crewId.getId());
        return new ReactionMotionMessage(
                partyroomId,
                MessageTopic.REACTION_MOTION,
                motionType,
                crew
        );
    }
}