package com.pfplaybackend.api.party.adapter.in.listener.message;

import com.pfplaybackend.api.party.application.dto.playback.PlaybackDto;
import com.pfplaybackend.api.common.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.domain.value.PartyroomId;

import java.io.Serializable;

public record PlaybackStartMessage(
        PartyroomId partyroomId,
        MessageTopic eventType,
        long crewId,
        PlaybackDto playback
) implements Serializable, GroupBroadcastMessage {}
