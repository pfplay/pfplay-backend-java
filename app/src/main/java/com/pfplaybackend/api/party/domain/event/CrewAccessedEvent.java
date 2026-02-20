package com.pfplaybackend.api.party.domain.event;

import com.pfplaybackend.api.common.domain.event.DomainEvent;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.party.domain.enums.AccessType;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import lombok.Getter;

@Getter
public class CrewAccessedEvent extends DomainEvent {
    private final PartyroomId partyroomId;
    private final long crewId;
    private final UserId userId;
    private final AccessType accessType;

    public CrewAccessedEvent(PartyroomId partyroomId, long crewId, UserId userId, AccessType accessType) {
        this.partyroomId = partyroomId;
        this.crewId = crewId;
        this.userId = userId;
        this.accessType = accessType;
    }

    @Override
    public String getAggregateId() {
        return String.valueOf(partyroomId.getId());
    }
}
