package com.pfplaybackend.api.party.domain.event;

import com.pfplaybackend.api.common.domain.event.DomainEvent;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import lombok.Getter;

@Getter
public class CrewGradeChangedEvent extends DomainEvent {
    private final PartyroomId partyroomId;
    private final CrewId adjusterCrewId;
    private final CrewId adjustedCrewId;
    private final GradeType prevGrade;
    private final GradeType currGrade;

    public CrewGradeChangedEvent(PartyroomId partyroomId, CrewId adjusterCrewId, CrewId adjustedCrewId,
                                  GradeType prevGrade, GradeType currGrade) {
        this.partyroomId = partyroomId;
        this.adjusterCrewId = adjusterCrewId;
        this.adjustedCrewId = adjustedCrewId;
        this.prevGrade = prevGrade;
        this.currGrade = currGrade;
    }
}
