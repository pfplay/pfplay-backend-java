package com.pfplaybackend.api.party.adapter.in.listener.message;

import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CrewGradeMessage implements Serializable {
    private PartyroomId partyroomId;
    private MessageTopic eventType;
    private Map<String, Object> adjuster;
    private Map<String, Object> adjusted;

    public static CrewGradeMessage from(PartyroomId partyroomId,
                                        CrewId adjusterCrewId, CrewId adjustedCrewId, GradeType prevGradeType, GradeType currGradeType) {
        Map<String, Object> adjuster = new HashMap<>();
        adjuster.put("crewId", adjusterCrewId.getId());

        Map<String, Object> adjusted = new HashMap<>();
        adjusted.put("crewId", adjustedCrewId.getId());
        adjusted.put("prevGradeType", prevGradeType);
        adjusted.put("currGradeType", currGradeType);

        return new CrewGradeMessage(
                partyroomId,
                MessageTopic.CREW_GRADE,
                adjuster,
                adjusted
        );
    }
}
