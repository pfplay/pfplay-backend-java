package com.pfplaybackend.api.partyroom.event.message;

import com.pfplaybackend.api.partyroom.application.dto.PartyroomSessionDto;
import com.pfplaybackend.api.partyroom.domain.enums.MessageTopic;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OutgoingGroupChatMessage {
    private PartyroomId partyroomId;
    private MessageTopic eventType;
    private Map<String, Object> member;
    private String message;

    public static OutgoingGroupChatMessage from(PartyroomSessionDto sessionDto, String message) {
        Map<String, Object> member = new HashMap<>();
        member.put("memberId", sessionDto.getMemberId());
        return new OutgoingGroupChatMessage(
                sessionDto.getPartyroomId(),
                MessageTopic.CHAT,
                member,
                message
        );
    }
}