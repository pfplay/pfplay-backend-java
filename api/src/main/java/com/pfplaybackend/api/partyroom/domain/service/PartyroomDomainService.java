package com.pfplaybackend.api.partyroom.domain.service;

import com.pfplaybackend.api.partyroom.domain.model.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.partyroom.repository.PartyroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartyroomDomainService {

    private final PartyroomRepository partyroomRepository;

    public boolean isChangedToActivation() {
        return true;
    }

    public boolean isNotInPartyroom() {
        return true;
    }

    public boolean isExistInDJQueue() {
        return true;
    }

    public boolean isQualifiedToCreatePartyroom() {
        return true;
    }

    public boolean isLinkAddressDuplicated(String suffixUri) {
        // Suffix
        return false;
    }
}
