package com.pfplaybackend.api.party.domain.specification;

import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.exception.PartyroomException;
import com.pfplaybackend.api.party.domain.exception.PenaltyException;

import java.util.Optional;

public class PartyroomEntrySpecification {

    public void validate(PartyroomData partyroom, long activeCrewCount, Optional<CrewData> existingCrew) {
        partyroom.validateNotTerminated();
        if (activeCrewCount > 49) throw ExceptionCreator.create(PartyroomException.EXCEEDED_LIMIT);
        existingCrew.filter(CrewData::isBanned).ifPresent(c -> {
            throw ExceptionCreator.create(PenaltyException.PERMANENT_EXPULSION);
        });
    }
}
