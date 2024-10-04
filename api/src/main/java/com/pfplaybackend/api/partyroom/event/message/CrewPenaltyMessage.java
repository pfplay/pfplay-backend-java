package com.pfplaybackend.api.partyroom.event.message;

import com.pfplaybackend.api.partyroom.domain.enums.PenaltyType;
import com.pfplaybackend.api.partyroom.domain.value.CrewId;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.event.MessageTopic;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CrewPenaltyMessage implements Serializable {
    private PartyroomId partyroomId;
    private MessageTopic eventType;
    private PenaltyType penaltyType;
    private String reason;
    private Map<String, Object> punisher;
    private Map<String, Object> punished;

    public static CrewPenaltyMessage from(PartyroomId partyroomId,
                                        CrewId punisherCrewId, CrewId punishedCrewId, String reason, PenaltyType penaltyType) {
        Map<String, Object> punisher = new HashMap<>();
        punisher.put("crewId", punisherCrewId.getId());

        Map<String, Object> punished = new HashMap<>();
        punished.put("crewId", punishedCrewId.getId());

        return new CrewPenaltyMessage(
                partyroomId,
                MessageTopic.CREW_GRADE,
                penaltyType,
                reason,
                punisher,
                punished
        );
    }
}
