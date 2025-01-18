package com.pfplaybackend.api.party.domain.service;

import com.pfplaybackend.api.party.domain.entity.domainmodel.Dj;
import com.pfplaybackend.api.party.domain.entity.domainmodel.Partyroom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DjDomainService {

    public boolean isExistDj(Partyroom partyroom) {
        Optional<Dj> optional = partyroom.getDjSet().stream().filter(Dj::isQueued).findAny();
        return optional.isPresent();
    }
}
