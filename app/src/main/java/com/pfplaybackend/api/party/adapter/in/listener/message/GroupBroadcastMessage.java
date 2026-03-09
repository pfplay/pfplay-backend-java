package com.pfplaybackend.api.party.adapter.in.listener.message;

import com.pfplaybackend.api.common.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.domain.value.PartyroomId;

public interface GroupBroadcastMessage {
    PartyroomId partyroomId();
    MessageTopic eventType();
    String id();
    long timestamp();
}
