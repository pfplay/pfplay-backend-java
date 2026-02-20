package com.pfplaybackend.api.party.domain.event;

import com.pfplaybackend.api.common.domain.event.DomainEvent;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import lombok.Getter;

@Getter
public class DjQueueChangedEvent extends DomainEvent {
    private final PartyroomId partyroomId;

    public DjQueueChangedEvent(PartyroomId partyroomId) {
        this.partyroomId = partyroomId;
    }

    @Override
    public String getAggregateId() {
        return String.valueOf(partyroomId.getId());
    }
}
