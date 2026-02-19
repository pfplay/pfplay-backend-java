package com.pfplaybackend.api.party.adapter.in.listener.message;

import com.pfplaybackend.api.party.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PartyroomClosedMessage implements Serializable {
    private PartyroomId partyroomId;
    private MessageTopic eventType;
}
