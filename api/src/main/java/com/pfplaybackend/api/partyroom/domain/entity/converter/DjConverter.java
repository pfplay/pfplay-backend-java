package com.pfplaybackend.api.partyroom.domain.entity.converter;

import com.pfplaybackend.api.partyroom.domain.entity.data.DjData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Dj;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DjConverter {
    public Dj toDomain(DjData data) {
        return new Dj();
    }
    public DjData toData(Dj dj) {
        return new DjData();
    }
}
