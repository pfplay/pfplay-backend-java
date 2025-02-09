package com.pfplaybackend.api.party.interfaces.listener.redis.message;

import com.pfplaybackend.api.party.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PartyroomClosedMessage {
    private PartyroomId partyroomId;
    private MessageTopic eventType;
}
