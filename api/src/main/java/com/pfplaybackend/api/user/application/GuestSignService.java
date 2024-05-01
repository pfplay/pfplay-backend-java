package com.pfplaybackend.api.user.application;

import com.pfplaybackend.api.config.jwt.JwtProvider;
import com.pfplaybackend.api.user.model.domain.GuestDomain;
import com.pfplaybackend.api.user.model.entity.Guest;
import com.pfplaybackend.api.user.repository.GuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GuestSignService {

    private final GuestRepository guestRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public Guest createGuest(String agent) {
        GuestDomain guestDomain = GuestDomain.create(agent);
        Guest guest = guestDomain.toEntity();
        return guestRepository.save(guest);
    }

}
