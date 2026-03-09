package com.pfplaybackend.api.party.adapter.in.listener.message;

import com.pfplaybackend.api.common.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.domain.value.PartyroomId;

import java.io.Serializable;
import java.util.UUID;

public record CrewExitedMessage(
        PartyroomId partyroomId,
        MessageTopic eventType,
        String id,
        long timestamp,
        long crewId
) implements Serializable, GroupBroadcastMessage {

    public static CrewExitedMessage create(PartyroomId partyroomId, long crewId) {
        return new CrewExitedMessage(partyroomId, MessageTopic.CREW_EXITED,
                UUID.randomUUID().toString(), System.currentTimeMillis(), crewId);
    }
}
