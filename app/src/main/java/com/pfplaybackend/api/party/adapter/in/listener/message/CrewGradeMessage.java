package com.pfplaybackend.api.party.adapter.in.listener.message;

import com.pfplaybackend.api.common.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;

import java.io.Serializable;
import java.util.UUID;

public record CrewGradeMessage(
        PartyroomId partyroomId,
        MessageTopic eventType,
        String id,
        long timestamp,
        AdjusterInfo adjuster,
        AdjustedInfo adjusted
) implements Serializable, GroupBroadcastMessage {

    public record AdjusterInfo(long crewId) {}
    public record AdjustedInfo(long crewId, GradeType prevGradeType, GradeType currGradeType) {}

    public static CrewGradeMessage from(PartyroomId partyroomId,
                                        CrewId adjusterCrewId, CrewId adjustedCrewId, GradeType prevGradeType, GradeType currGradeType) {
        return new CrewGradeMessage(
                partyroomId,
                MessageTopic.CREW_GRADE_CHANGED,
                UUID.randomUUID().toString(),
                System.currentTimeMillis(),
                new AdjusterInfo(adjusterCrewId.getId()),
                new AdjustedInfo(adjustedCrewId.getId(), prevGradeType, currGradeType)
        );
    }
}
