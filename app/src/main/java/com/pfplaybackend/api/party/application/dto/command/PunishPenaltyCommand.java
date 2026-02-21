package com.pfplaybackend.api.party.application.dto.command;

import com.pfplaybackend.api.party.domain.enums.PenaltyType;

public record PunishPenaltyCommand(long crewId, PenaltyType penaltyType, String detail) {}
