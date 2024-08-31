package com.pfplaybackend.api.config.websocket.application.service;

import com.pfplaybackend.api.config.websocket.domain.entity.data.PartyroomSessionData;
import com.pfplaybackend.api.config.websocket.domain.entity.domainmodel.PartyroomSession;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.config.websocket.repository.SessionRepository;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SessionEventService {
    private final SessionRepository sessionRepository;

    public Optional<PartyroomSession> getPartyroomSession(String sessionId) {
        Optional<PartyroomSessionData> optional = sessionRepository.getPartyroomSessionData(sessionId);
        if (optional.isPresent()) {
            final PartyroomSessionData partyroomSessionData = optional.get();
            return Optional.of(partyroomSessionData.toDomain());
        }
        return Optional.empty();
    }

    @Transactional
    public void saveSession(String sessionId, UserId userId, PartyroomId partyroomId) {
        PartyroomSessionData partyroomSessionData = PartyroomSessionData.create(
                sessionId, userId, partyroomId
        );

        sessionRepository.savePartyroomSessionData(sessionId, partyroomSessionData);
    }
}
