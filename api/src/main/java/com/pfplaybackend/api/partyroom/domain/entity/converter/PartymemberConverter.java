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
        return new Partymember();
    }

    public PartymemberData toData(Partymember partymember, PartyroomData partyroomData) {
        return new PartymemberData();
        //        return PartymemberData.builder()
//                .id(partymember.getId())
//                .partyroomData(partyroomData)
//                .authorityTier(partymember.getAuthorityTier())
//                .userId(partymember.getUserId())
//                .build();
    }
}
