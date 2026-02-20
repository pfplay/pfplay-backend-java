package com.pfplaybackend.api.party.domain.specification;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.exception.GradeException;

public class GradeAdjustmentSpecification {

    public void validate(CrewData adjuster, CrewData subject, GradeType targetGrade, AuthorityTier subjectTier) {
        if (adjuster.isBelowGrade(GradeType.MODERATOR)) throw ExceptionCreator.create(GradeException.MANAGER_GRADE_REQUIRED);
        if (!targetGrade.equals(GradeType.LISTENER) && subjectTier == AuthorityTier.GT) throw ExceptionCreator.create(GradeException.GUEST_ONLY_POSSIBLE_LISTENER);
        if (targetGrade == GradeType.HOST) throw ExceptionCreator.create(GradeException.UNABLE_TO_SET_HOST);
        if (adjuster.getGradeType().isLowerThan(subject.getGradeType())) throw ExceptionCreator.create(GradeException.GRADE_INSUFFICIENT_FOR_OPERATION);
        if (targetGrade.isEqualOrHigherThan(adjuster.getGradeType())) throw ExceptionCreator.create(GradeException.GRADE_EXCEEDS_ALLOWED_THRESHOLD);
    }
}
