package com.pfplaybackend.api.party.application.dto.command;

import com.pfplaybackend.api.party.domain.enums.QueueStatus;

public record UpdateDjQueueStatusCommand(QueueStatus queueStatus) {}
