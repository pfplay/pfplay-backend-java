package com.pfplaybackend.api.party.adapter.in.listener.message;

import com.pfplaybackend.api.party.application.dto.crew.CrewSummaryDto;
import com.pfplaybackend.api.party.domain.enums.AccessType;
import com.pfplaybackend.api.common.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.domain.value.PartyroomId;

import java.io.Serializable;

public record PartyroomAccessMessage(
        PartyroomId partyroomId,
        MessageTopic eventType,
        AccessType accessType,
        CrewSummaryDto crew
) implements Serializable, GroupBroadcastMessage {

    public static PartyroomAccessMessage create(PartyroomId partyroomId, AccessType accessType, CrewSummaryDto crewSummaryDto) {
        return new PartyroomAccessMessage(partyroomId, MessageTopic.PARTYROOM_ACCESS, accessType, crewSummaryDto);
    }
}
