package com.pfplaybackend.api.user.application.service;

import com.pfplaybackend.api.profile.application.service.UserProfileService;
import com.pfplaybackend.api.profile.domain.ProfileData;
import com.pfplaybackend.api.user.domain.entity.data.GuestData;
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
    public GuestData getGuestOrCreate() {
        GuestData guest = GuestData.create();
        ProfileData profile = userProfileService.createProfileDataForGuest(guest.getUserId());
        guest.initiateProfile(profile);
        return guestRepository.save(guest);
    }
}
