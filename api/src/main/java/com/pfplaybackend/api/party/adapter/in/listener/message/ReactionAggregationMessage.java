package com.pfplaybackend.api.party.adapter.in.listener.message;

import com.pfplaybackend.api.party.application.dto.playback.AggregationDto;
import com.pfplaybackend.api.common.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.domain.value.PartyroomId;

import java.io.Serializable;

public record ReactionAggregationMessage(
        PartyroomId partyroomId,
        MessageTopic eventType,
        AggregationDto aggregation
) implements Serializable, GroupBroadcastMessage {}
