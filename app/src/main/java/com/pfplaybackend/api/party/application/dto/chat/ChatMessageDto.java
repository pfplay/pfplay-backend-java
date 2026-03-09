package com.pfplaybackend.api.party.application.dto.chat;

import com.pfplaybackend.api.common.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.application.dto.partyroom.PartyroomSessionDto;
import com.pfplaybackend.api.party.domain.value.PartyroomId;

import java.io.Serializable;
import java.util.UUID;

public record ChatMessageDto(
        PartyroomId partyroomId,
        MessageTopic eventType,
        String id,
        long timestamp,
        CrewInfo crew,
        ChatContent message
) implements Serializable {

    public record CrewInfo(long crewId) {}
    public record ChatContent(String messageId, String content) {}

    public static ChatMessageDto from(PartyroomSessionDto sessionDto, String content, long timestamp) {
        return new ChatMessageDto(
                sessionDto.partyroomId(),
                MessageTopic.CHAT_MESSAGE_SENT,
                UUID.randomUUID().toString(),
                System.currentTimeMillis(),
                new CrewInfo(sessionDto.crewId()),
                new ChatContent(timestamp + ":" + sessionDto.crewId(), content)
        );
    }

    public static ChatMessageDto from(PartyroomSessionDto sessionDto, String content) {
        return from(sessionDto, content, System.currentTimeMillis());
    }
}
