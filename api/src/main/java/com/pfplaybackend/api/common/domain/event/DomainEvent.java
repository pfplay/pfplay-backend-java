package com.pfplaybackend.api.common.domain.event;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public abstract class DomainEvent {
    private final LocalDateTime occurredAt = LocalDateTime.now();
}
