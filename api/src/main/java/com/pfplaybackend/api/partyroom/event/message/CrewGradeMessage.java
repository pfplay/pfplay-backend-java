package com.pfplaybackend.api.partyroom.event.message;

import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import com.pfplaybackend.api.partyroom.event.MessageTopic;
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
public class CrewGradeMessage implements Serializable {
    private PartyroomId partyroomId;
    private MessageTopic eventType;
    private Map<String, Object> crew;

    public static CrewGradeMessage from(PartyroomId partyroomId,
                                        CrewId crewId, GradeType prevGradeType, GradeType currGradeType) {
        Map<String, Object> crew = new HashMap<>();
        crew.put("crewId", crewId.getId());
        crew.put("prevGradeType", prevGradeType);
        crew.put("currGradeType", currGradeType);

        return new CrewGradeMessage(
                partyroomId,
                MessageTopic.CREW_GRADE,
                crew
        );
    }
}
