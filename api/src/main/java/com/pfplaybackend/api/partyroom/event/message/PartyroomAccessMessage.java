package com.pfplaybackend.api.partyroom.event.message;

import com.pfplaybackend.api.partyroom.application.dto.crew.CrewSummaryDto;
import com.pfplaybackend.api.partyroom.domain.enums.AccessType;
import com.pfplaybackend.api.partyroom.event.MessageTopic;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PartyroomAccessMessage implements Serializable {
    private PartyroomId partyroomId;
    private MessageTopic eventType;
    private AccessType accessType;
    private CrewSummaryDto crew;

    public static PartyroomAccessMessage create(PartyroomId partyroomId, AccessType accessType, CrewSummaryDto crewSummaryDto) {
        return new PartyroomAccessMessage(
                partyroomId,
                MessageTopic.PARTYROOM_ACCESS,
                accessType,
                crewSummaryDto);
    }
}