package com.pfplaybackend.api.party.domain.event;

import com.pfplaybackend.api.common.domain.event.DomainEvent;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import lombok.Getter;

@Getter
public class PartyroomClosedEvent extends DomainEvent {
    private final PartyroomId partyroomId;

    public PartyroomClosedEvent(PartyroomId partyroomId) {
        this.partyroomId = partyroomId;
    }
}
