package com.pfplaybackend.api.user.domain.service;

import com.pfplaybackend.api.common.util.CustomStringUtils;
import org.springframework.stereotype.Service;

@Service
public class GuestDomainService {

    final String GUEST_NICKNAME_PREFIX = "Guest_";

    public String generateRandomNickname() {
        return GUEST_NICKNAME_PREFIX + CustomStringUtils.getRandomUuidWithoutHyphen().substring(0, 6);
    }
}
