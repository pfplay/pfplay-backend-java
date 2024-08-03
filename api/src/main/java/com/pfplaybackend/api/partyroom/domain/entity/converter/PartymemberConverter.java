package com.pfplaybackend.api.partyroom.domain.entity.converter;

import com.pfplaybackend.api.partyroom.domain.entity.data.PartymemberData;
import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partymember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartymemberConverter {
    public Partymember toDomain(PartymemberData partymemberData) {
        return Partymember.builder()
                .id(partymemberData.getId())
                .userId(partymemberData.getUserId())
                .authorityTier(partymemberData.getAuthorityTier())
                .gradeType(partymemberData.getGradeType())
                .isActive(partymemberData.isActive())
                .isBanned(partymemberData.isBanned())
                .enteredAt(partymemberData.getEnteredAt())
                .exitedAt(partymemberData.getExitedAt())
                .build();
    }

    public PartymemberData toData(Partymember partymember) {
        return PartymemberData.builder()
                .id(partymember.getId())
                .userId(partymember.getUserId())
                .authorityTier(partymember.getAuthorityTier())
                .gradeType(partymember.getGradeType())
                .isActive(partymember.isActive())
                .isBanned(partymember.isBanned())
                .enteredAt(partymember.getEnteredAt())
                .exitedAt(partymember.getExitedAt())
                .build();
    }
}
