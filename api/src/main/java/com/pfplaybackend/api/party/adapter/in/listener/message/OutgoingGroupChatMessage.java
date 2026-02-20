package com.pfplaybackend.api.party.adapter.in.listener.message;

import com.pfplaybackend.api.party.application.dto.partyroom.PartyroomSessionDto;
import com.pfplaybackend.api.common.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.domain.value.PartyroomId;

import java.io.Serializable;

public record OutgoingGroupChatMessage(
        PartyroomId partyroomId,
        MessageTopic eventType,
        CrewInfo crew,
        ChatContent message
) implements Serializable, GroupBroadcastMessage {

    public record CrewInfo(long crewId) {}
    public record ChatContent(String messageId, String content) {}

    public static OutgoingGroupChatMessage from(PartyroomSessionDto sessionDto, String content) {
        return new OutgoingGroupChatMessage(
                sessionDto.partyroomId(),
                MessageTopic.CHAT,
                new CrewInfo(sessionDto.crewId()),
                new ChatContent(System.currentTimeMillis() + ":" + sessionDto.crewId(), content)
        );
    }
}
