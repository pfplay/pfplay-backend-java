package com.pfplaybackend.api.party.adapter.in.listener.message;

import com.pfplaybackend.api.common.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.application.dto.crew.CrewSummaryDto;
import com.pfplaybackend.api.party.domain.value.PartyroomId;

import java.io.Serializable;
import java.util.UUID;

public record CrewEnteredMessage(
        PartyroomId partyroomId,
        MessageTopic eventType,
        String id,
        long timestamp,
        CrewSummaryDto crew
) implements Serializable, GroupBroadcastMessage {

    public static CrewEnteredMessage create(PartyroomId partyroomId, CrewSummaryDto crew) {
        return new CrewEnteredMessage(partyroomId, MessageTopic.CREW_ENTERED,
                UUID.randomUUID().toString(), System.currentTimeMillis(), crew);
    }
}
