package com.pfplaybackend.api.config.websocket.service;

import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomSessionData;
import com.pfplaybackend.api.partyroom.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionEventService {
    private final SessionRepository sessionRepository;
    public void saveSession(String sessionId, String destination, UUID uid) {
        sessionRepository.save(
                PartyroomSessionData.create(
                        sessionId,
                        destination,
                        uid
                )
        );
    }
}
