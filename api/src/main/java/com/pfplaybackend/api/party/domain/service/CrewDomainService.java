package com.pfplaybackend.api.party.domain.service;

import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.adapter.out.persistence.CrewRepository;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CrewDomainService {

    private final CrewRepository crewRepository;

    public boolean isExistEnterHistory() {
        return false;
    }

    public boolean isBelowManagerGrade(Long partyroomId, UserId adjuster) {
        CrewData aCrew = crewRepository.findByPartyroomDataIdAndUserId(partyroomId, adjuster).orElseThrow();
        return aCrew.getGradeType().isLowerThan(GradeType.MODERATOR);
    }

    public boolean isAdjusterGradeLowerThanSubject(Long partyroomId, UserId adjuster, CrewId subject) {
        CrewData aCrew = crewRepository.findByPartyroomDataIdAndUserId(partyroomId, adjuster).orElseThrow();
        CrewData sCrew = crewRepository.findById(subject.getId()).orElseThrow();
        return aCrew.getGradeType().isLowerThan(sCrew.getGradeType());
    }

    public boolean isTargetGradeExceedingAdjuster(Long partyroomId, UserId adjuster, GradeType targetGradeType) {
        CrewData aCrew = crewRepository.findByPartyroomDataIdAndUserId(partyroomId, adjuster).orElseThrow();
        return targetGradeType.isEqualOrHigherThan(aCrew.getGradeType());
    }
}
