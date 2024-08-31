package com.pfplaybackend.api.config.websocket.service;

import com.pfplaybackend.api.partyroom.domain.entity.data.PartymemberData;
import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomSessionData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.PartyroomSession;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.exception.InvalidPartymemberException;
import com.pfplaybackend.api.partyroom.repository.PartymemberRepository;
import com.pfplaybackend.api.partyroom.repository.custom.SessionRepository;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionEventService {
    private static final Logger logger = LoggerFactory.getLogger(SessionEventService.class);
    private final SessionRepository sessionRepository;
    private final PartymemberRepository partymemberRepository;



    public Optional<PartyroomSession> getPartyroomSession(String sessionId) {
        Optional<PartyroomSessionData> data = sessionRepository.getPartyroomSessionData(sessionId);
        if (data.isPresent()) {
            final PartyroomSessionData partyroomSessionData = data.get();
            return Optional.of(partyroomSessionData.toDomain());
        }
        return Optional.empty();
    }

    public PartyroomId getPartyroomId(UUID uid) {
        Optional<PartymemberData> partymemberData = partymemberRepository.findByUserIdUid(uid);
        if (partymemberData.isPresent()) {
            return partymemberData.get().getPartyroomData().getPartyroomId();
        }
        throw new InvalidPartymemberException();
    }
}
