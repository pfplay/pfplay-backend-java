package com.pfplaybackend.api.partyroom.domain.service;

import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Dj;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DjDomainService {

    public boolean isExistDj(Partyroom partyroom) {
        return partyroom.getDjSet().stream().noneMatch(Dj::isDeleted);
    }
}
