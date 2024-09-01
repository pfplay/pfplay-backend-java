package com.pfplaybackend.api.partyroom.domain.service;

import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partymember;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import com.pfplaybackend.api.partyroom.domain.value.PartymemberId;
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
        Partymember aCrew = partyroom.getPartymemberByUserId(adjuster).orElseThrow();
        return aCrew.getGradeType().isLowerThan(GradeType.MODERATOR);
    }

    public boolean isAdjusterGradeLowerThanSubject(Partyroom partyroom, UserId adjuster, PartymemberId subject) {
        Partymember aCrew = partyroom.getPartymemberByUserId(adjuster).orElseThrow();
        Partymember sCrew = partyroom.getPartymember(subject);
        return aCrew.getGradeType().isLowerThan(sCrew.getGradeType());
    }

    public boolean isTargetGradeExceedingAdjuster(Partyroom partyroom, UserId adjuster, GradeType targetGradeType) {
        Partymember aCrew = partyroom.getPartymemberByUserId(adjuster).orElseThrow();
        return targetGradeType.isEqualOrHigherThan(aCrew.getGradeType());
    }
}
