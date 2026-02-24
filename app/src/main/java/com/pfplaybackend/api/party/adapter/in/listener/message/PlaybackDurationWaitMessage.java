package com.pfplaybackend.api.party.adapter.in.listener.message;

import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;

import java.io.Serializable;

public record PlaybackDurationWaitMessage(
        PartyroomId partyroomId,
        UserId userId
) implements Serializable {}
