package com.pfplaybackend.api.party.adapter.in.listener.message;

import com.pfplaybackend.api.common.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.domain.value.PartyroomId;

import java.io.Serializable;

public record PartyroomClosedMessage(
        PartyroomId partyroomId,
        MessageTopic eventType
) implements Serializable, GroupBroadcastMessage {}
