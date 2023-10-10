package com.pfplaybackend.api.guest.service;

import com.pfplaybackend.api.common.util.CustomStringUtils;
import com.pfplaybackend.api.entity.Guest;
import com.pfplaybackend.api.guest.repository.GuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GuestService {

    private final GuestRepository guestRepository;
    private final String GUEST_NAME_PREFIX = "Guest_";

    @Transactional
    public Guest createGuest(String agent) {
        final String uuid = CustomStringUtils.getRandomUuidWithoutHyphen().substring(0, 6);

        Guest guest = Guest.builder()
                .name(GUEST_NAME_PREFIX + uuid)
                .agent(agent)
                .build();

        return guestRepository.save(guest);
    }

}
