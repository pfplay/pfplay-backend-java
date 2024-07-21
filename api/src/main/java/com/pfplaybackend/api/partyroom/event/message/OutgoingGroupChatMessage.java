package com.pfplaybackend.api.partyroom.event.message;

import com.pfplaybackend.api.partyroom.domain.enums.MessageTopic;
import com.pfplaybackend.api.partyroom.domain.enums.MotionType;
import com.pfplaybackend.api.partyroom.domain.value.PartymemberId;
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

    public static OutgoingGroupChatMessage from(Map<String, Object> map, String message) {
        Integer partyroomId = (Integer) map.get("partyroomId");
        Map<String, Object> member = new HashMap<>();
        member.put("memberId", map.get("memberId"));
        return new OutgoingGroupChatMessage(
                new PartyroomId(partyroomId),
                MessageTopic.CHAT,
                member,
                message
        );
    }
}