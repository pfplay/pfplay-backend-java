package com.pfplaybackend.api.party.domain.event;

import com.pfplaybackend.api.common.domain.event.DomainEvent;
import com.pfplaybackend.api.party.domain.enums.PenaltyType;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import lombok.Getter;

@Getter
public class CrewPenalizedEvent extends DomainEvent {
    private final PartyroomId partyroomId;
    private final CrewId punisherCrewId;
    private final CrewId punishedCrewId;
    private final String detail;
    private final PenaltyType penaltyType;

    public CrewPenalizedEvent(PartyroomId partyroomId, CrewId punisherCrewId, CrewId punishedCrewId,
                               String detail, PenaltyType penaltyType) {
        this.partyroomId = partyroomId;
        this.punisherCrewId = punisherCrewId;
        this.punishedCrewId = punishedCrewId;
        this.detail = detail;
        this.penaltyType = penaltyType;
    }

    @Override
    public String getAggregateId() {
        return String.valueOf(partyroomId.getId());
    }
}
