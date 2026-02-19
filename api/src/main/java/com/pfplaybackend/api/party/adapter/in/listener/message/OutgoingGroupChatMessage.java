package com.pfplaybackend.api.party.adapter.in.listener.message;

import com.pfplaybackend.api.party.application.dto.partyroom.PartyroomSessionDto;
import com.pfplaybackend.api.party.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
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
