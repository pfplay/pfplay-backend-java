package com.pfplaybackend.api.user.application.service;

import com.pfplaybackend.api.user.domain.entity.domainmodel.Guest;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Profile;
import com.pfplaybackend.api.user.repository.GuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GuestSignService {

    private final GuestRepository guestRepository;
    private final UserProfileService userProfileService;

    @Transactional
    public Guest getGuestOrCreate(String userAgent) {
        Guest guest = Guest.create(userAgent);
        Profile profile = userProfileService.createProfileForGuest(guest);
        Guest updatedGuest = guest.initiateProfile(profile);
        guestRepository.save(updatedGuest.toData());
        return updatedGuest;
    }
}
