package com.pfplaybackend.api.party.domain.service;

import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CrewDomainService {

    public boolean isExistEnterHistory() {
        return false;
    }

    public boolean isBelowManagerGrade(PartyroomData partyroom, UserId adjuster) {
        CrewData aCrew = partyroom.getCrewByUserId(adjuster).orElseThrow();
        return aCrew.getGradeType().isLowerThan(GradeType.MODERATOR);
    }

    public boolean isAdjusterGradeLowerThanSubject(PartyroomData partyroom, UserId adjuster, CrewId subject) {
        CrewData aCrew = partyroom.getCrewByUserId(adjuster).orElseThrow();
        CrewData sCrew = partyroom.getCrew(subject);
        return aCrew.getGradeType().isLowerThan(sCrew.getGradeType());
    }

    public boolean isTargetGradeExceedingAdjuster(PartyroomData partyroom, UserId adjuster, GradeType targetGradeType) {
        CrewData aCrew = partyroom.getCrewByUserId(adjuster).orElseThrow();
        return targetGradeType.isEqualOrHigherThan(aCrew.getGradeType());
    }
}
