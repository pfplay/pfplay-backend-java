package com.pfplaybackend.api.user.service.user;

import com.pfplaybackend.api.common.util.CustomStringUtils;
import com.pfplaybackend.api.user.model.entity.user.Guest;
import com.pfplaybackend.api.user.repository.user.GuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GuestService {

    private final GuestRepository guestRepository;

    @Transactional
    public Guest createGuest(String agent) {
        final String uuid = CustomStringUtils.getRandomUuidWithoutHyphen().substring(0, 6);

        String GUEST_NAME_PREFIX = "Guest_";
        Guest guest = Guest.builder()
                .name(GUEST_NAME_PREFIX + uuid)
                .agent(agent)
                .build();

        return guestRepository.save(guest);
    }

}
