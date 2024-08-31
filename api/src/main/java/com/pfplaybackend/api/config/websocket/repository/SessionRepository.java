package com.pfplaybackend.api.config.websocket.repository;

import com.pfplaybackend.api.config.websocket.domain.entity.data.PartyroomSessionData;

import java.util.Optional;

public interface SessionRepository {
    Optional<PartyroomSessionData> getPartyroomSessionData(String sessionId);
    PartyroomSessionData savePartyroomSessionData(String sessionId, PartyroomSessionData partyroomSessionData);
}
