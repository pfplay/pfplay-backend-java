package com.pfplaybackend.api.party.adapter.in.listener.message;

import com.pfplaybackend.api.common.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.value.PlaybackSnapshot;

import java.io.Serializable;

public record PlaybackStartMessage(
        PartyroomId partyroomId,
        MessageTopic eventType,
        String id,
        long timestamp,
        long crewId,
        PlaybackSnapshot playback
) implements Serializable, GroupBroadcastMessage {}
