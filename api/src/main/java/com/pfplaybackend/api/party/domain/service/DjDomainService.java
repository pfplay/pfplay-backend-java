package com.pfplaybackend.api.party.domain.service;

import com.pfplaybackend.api.party.domain.entity.data.DjData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DjDomainService {

    public boolean isExistDj(PartyroomData partyroom) {
        Optional<DjData> optional = partyroom.getDjDataSet().stream().filter(DjData::isQueued).findAny();
        return optional.isPresent();
    }
}
