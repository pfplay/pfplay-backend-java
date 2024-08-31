package com.pfplaybackend.api.partyroom.repository.custom;

import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomSessionData;

import java.util.Optional;

public interface SessionRepository {
    Optional<PartyroomSessionData> getPartyroomSessionData(String sessionId);
    PartyroomSessionData savePartyroomSessionData(PartyroomSessionData partyroomSessionData);
}
