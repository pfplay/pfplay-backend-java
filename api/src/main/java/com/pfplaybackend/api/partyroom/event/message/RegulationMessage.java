package com.pfplaybackend.api.partyroom.event.message;

import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import com.pfplaybackend.api.partyroom.domain.enums.MessageTopic;
import com.pfplaybackend.api.partyroom.domain.enums.RegulationType;
import com.pfplaybackend.api.partyroom.domain.value.CrewId;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegulationMessage {
    private PartyroomId partyroomId;
    private MessageTopic eventType;
    private RegulationType regulationType;
    private Map<String, Object> crew;

    public static RegulationMessage from(PartyroomId partyroomId, RegulationType regulationType,
                                         CrewId crewId, GradeType prevGradeType, GradeType currGradeType) {
        Map<String, Object> crew = new HashMap<>();
        crew.put("crewId", crewId.getId());
        crew.put("prevGradeType", prevGradeType);
        crew.put("currGradeType", currGradeType);

        return new RegulationMessage(
                partyroomId,
                MessageTopic.REGULATION,
                regulationType,
                crew
        );
    }
}
