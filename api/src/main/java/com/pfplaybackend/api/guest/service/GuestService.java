package com.pfplaybackend.api.guest.service;

import com.pfplaybackend.api.entity.Guest;
import com.pfplaybackend.api.guest.repository.GuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GuestService {

    private final GuestRepository guestRepository;
    private final String GUEST_NAME_PREFIX = "Guest_";

    @Transactional
    public Guest createGuest(String agent) {
        final String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 6);

        Guest guest = Guest.builder()
                .name(GUEST_NAME_PREFIX + uuid)
                .agent(agent)
                .build();

        return guestRepository.save(guest);
    }

}