package com.pfplaybackend.api.partyroom.domain.entity.converter;

import com.pfplaybackend.api.partyroom.domain.entity.data.CrewData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Crew;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CrewConverter {
    public Crew toDomain(CrewData crewData) {
        return Crew.builder()
                .id(crewData.getId())
                .userId(crewData.getUserId())
                .authorityTier(crewData.getAuthorityTier())
                .gradeType(crewData.getGradeType())
                .isActive(crewData.isActive())
                .isBanned(crewData.isBanned())
                .enteredAt(crewData.getEnteredAt())
                .exitedAt(crewData.getExitedAt())
                .build();
    }

    public CrewData toData(Crew crew) {
        return CrewData.builder()
                .id(crew.getId())
                .userId(crew.getUserId())
                .authorityTier(crew.getAuthorityTier())
                .gradeType(crew.getGradeType())
                .isActive(crew.isActive())
                .isBanned(crew.isBanned())
                .enteredAt(crew.getEnteredAt())
                .exitedAt(crew.getExitedAt())
                .build();
    }
}
