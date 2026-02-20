package com.pfplaybackend.api.party.domain.specification;

import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.exception.DjException;

public class DjEnqueueSpecification {

    public void validate(PartyroomData partyroom, boolean isAlreadyRegistered, boolean isEmptyPlaylist) {
        partyroom.validateQueueOpen();
        if (isEmptyPlaylist) throw ExceptionCreator.create(DjException.EMPTY_PLAYLIST);
        if (isAlreadyRegistered) throw ExceptionCreator.create(DjException.ALREADY_REGISTERED);
    }
}
