package com.pfplaybackend.api.party.domain.event;

import com.pfplaybackend.api.common.domain.event.DomainEvent;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import lombok.Getter;

@Getter
public class PartyroomClosedEvent extends DomainEvent {
    private final PartyroomId partyroomId;
    private final UserId hostId;
    private final String title;

    public PartyroomClosedEvent(PartyroomId partyroomId, UserId hostId, String title) {
        this.partyroomId = partyroomId;
        this.hostId = hostId;
        this.title = title;
    }

    @Override
    public String getAggregateId() {
        return String.valueOf(partyroomId.getId());
    }
}
