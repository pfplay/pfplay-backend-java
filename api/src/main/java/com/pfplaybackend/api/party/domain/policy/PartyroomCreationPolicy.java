package com.pfplaybackend.api.party.domain.policy;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.party.domain.exception.PartyroomException;

public class PartyroomCreationPolicy {

    public void enforce(AuthorityTier authorityTier) {
        if (!authorityTier.equals(AuthorityTier.FM)) {
            throw ExceptionCreator.create(PartyroomException.RESTRICTED_AUTHORITY);
        }
    }
}
