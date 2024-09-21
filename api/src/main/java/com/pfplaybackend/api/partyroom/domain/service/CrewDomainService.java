package com.pfplaybackend.api.partyroom.domain.service;

import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Crew;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import com.pfplaybackend.api.partyroom.domain.value.CrewId;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CrewDomainService {

    public boolean isExistEnterHistory() {
        return false;
    }

    public boolean isBelowManagerGrade(Partyroom partyroom, UserId adjuster) {
        Crew aCrew = partyroom.getCrewByUserId(adjuster).orElseThrow();
        return aCrew.getGradeType().isLowerThan(GradeType.MODERATOR);
    }

    public boolean isAdjusterGradeLowerThanSubject(Partyroom partyroom, UserId adjuster, CrewId subject) {
        Crew aCrew = partyroom.getCrewByUserId(adjuster).orElseThrow();
        Crew sCrew = partyroom.getCrew(subject);
        return aCrew.getGradeType().isLowerThan(sCrew.getGradeType());
    }

    public boolean isTargetGradeExceedingAdjuster(Partyroom partyroom, UserId adjuster, GradeType targetGradeType) {
        Crew aCrew = partyroom.getCrewByUserId(adjuster).orElseThrow();
        return targetGradeType.isEqualOrHigherThan(aCrew.getGradeType());
    }
}
