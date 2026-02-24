package com.pfplaybackend.api.party.application.dto.partyroom;

import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;

import java.io.Serializable;

public record PartyroomSessionDto(String sessionId, UserId userId, PartyroomId partyroomId, long crewId) implements Serializable {}
