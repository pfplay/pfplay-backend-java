package com.pfplaybackend.api.common.domain.event;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public abstract class DomainEvent {
    private final UUID eventId = UUID.randomUUID();
    private final LocalDateTime occurredAt = LocalDateTime.now();
    private final String eventType;

    protected DomainEvent() {
        this.eventType = this.getClass().getSimpleName();
    }

    /** 이 이벤트를 발생시킨 Aggregate의 식별자 */
    public abstract String getAggregateId();
}
