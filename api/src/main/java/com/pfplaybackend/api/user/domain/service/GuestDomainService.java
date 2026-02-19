package com.pfplaybackend.api.user.domain.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GuestDomainService {

    final String GUEST_NICKNAME_PREFIX = "Guest_";

    public String generateRandomNickname() {
        return GUEST_NICKNAME_PREFIX + getRandomUuidWithoutHyphen().substring(0, 6);
    }

    private String getRandomUuidWithoutHyphen() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
