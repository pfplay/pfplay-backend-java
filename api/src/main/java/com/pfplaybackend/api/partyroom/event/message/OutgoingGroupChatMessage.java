package com.pfplaybackend.api.partyroom.event.message;

import com.pfplaybackend.api.partyroom.application.dto.partyroom.PartyroomSessionDto;
import com.pfplaybackend.api.partyroom.event.MessageTopic;
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
public class OutgoingGroupChatMessage implements Serializable {
    private PartyroomId partyroomId;
    private MessageTopic eventType;
    private Map<String, Object> crew;
    private Map<String, Object> message;

    public static OutgoingGroupChatMessage from(PartyroomSessionDto sessionDto, String content) {
        Map<String, Object> crew = new HashMap<>();
        crew.put("crewId", sessionDto.getCrewId());

        Map<String, Object> message = new HashMap<>();
        message.put("messageId", System.currentTimeMillis() + ":" + sessionDto.getCrewId());
        message.put("content", content);
        return new OutgoingGroupChatMessage(
                sessionDto.getPartyroomId(),
                MessageTopic.CHAT,
                crew,
                message
        );
    }
}