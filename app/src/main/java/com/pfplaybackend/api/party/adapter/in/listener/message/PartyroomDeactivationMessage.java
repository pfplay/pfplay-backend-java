package com.pfplaybackend.api.party.adapter.in.listener.message;

import com.pfplaybackend.api.common.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.domain.value.PartyroomId;

import java.io.Serializable;

public record PartyroomDeactivationMessage(
        PartyroomId partyroomId,
        MessageTopic eventType,
        String id,
        long timestamp
) implements Serializable, GroupBroadcastMessage {}
