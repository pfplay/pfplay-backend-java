package com.pfplaybackend.api.party.domain.event;

import com.pfplaybackend.api.common.domain.event.DomainEvent;
import com.pfplaybackend.api.party.domain.enums.DjChangeType;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import lombok.Getter;

@Getter
public class DjQueueChangedEvent extends DomainEvent {
    private final PartyroomId partyroomId;
    private final DjChangeType changeType;
    private final CrewId affectedCrewId;

    public DjQueueChangedEvent(PartyroomId partyroomId, DjChangeType changeType, CrewId affectedCrewId) {
        this.partyroomId = partyroomId;
        this.changeType = changeType;
        this.affectedCrewId = affectedCrewId;
    }

    @Override
    public String getAggregateId() {
        return String.valueOf(partyroomId.getId());
    }
}
