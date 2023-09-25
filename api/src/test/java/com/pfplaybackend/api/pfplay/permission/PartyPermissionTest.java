package com.pfplaybackend.api.pfplay.permission;

import com.pfplaybackend.api.config.ObjectMapperConfig;
import com.pfplaybackend.api.entity.PartyPermission;
import com.pfplaybackend.api.partyroom.enums.PartyPermissionRole;
import com.pfplaybackend.api.partyroom.presentation.dto.PartyRoomPermissionDefaultDto;
import com.pfplaybackend.api.partyroom.repository.PartyPermissionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
public class PartyPermissionTest {

    @Autowired
    PartyPermissionRepository partyPermissionRepository;

    @Autowired
    ObjectMapperConfig om;

    @Test
    @Transactional
    @Rollback(value = false)
    void createPartyPermission() {
        PartyPermission admin = PartyPermission
                .builder()
                .authority(PartyPermissionRole.ADMIN)
                .partyInfoFetch(true)
                .partyClose(true)
                .notice(true)
                .giveToClubber(true)
                .chatDelete(true)
                .chatLimitBanToClubber(true)
                .kickToClubber(true)
                .banToClubber(true)
                .chatBan(true)
                .djWaitLock(true)
                .newDj(true)
                .musicSkip(true)
                .videoLengthLimit(true)
                .build();

        PartyPermission cm = PartyPermission
                .builder()
                .authority(PartyPermissionRole.COMMUNITY_MANAGER)
                .partyInfoFetch(false)
                .partyClose(false)
                .notice(true)
                .giveToClubber(true)
                .chatDelete(true)
                .chatLimitBanToClubber(true)
                .kickToClubber(true)
                .banToClubber(true)
                .chatBan(true)
                .djWaitLock(true)
                .newDj(true)
                .musicSkip(true)
                .videoLengthLimit(true)
                .build();

        PartyPermission mod = PartyPermission
                .builder()
                .authority(PartyPermissionRole.COMMUNITY_MANAGER)
                .partyInfoFetch(false)
                .partyClose(false)
                .notice(false)
                .giveToClubber(true)
                .chatDelete(true)
                .chatLimitBanToClubber(true)
                .kickToClubber(true)
                .banToClubber(false)
                .chatBan(true)
                .djWaitLock(true)
                .newDj(true)
                .musicSkip(true)
                .videoLengthLimit(true)
                .build();

        PartyPermission clubber = PartyPermission
                .builder()
                .authority(PartyPermissionRole.CLUBBER)
                .partyInfoFetch(false)
                .partyClose(false)
                .notice(false)
                .giveToClubber(false)
                .chatDelete(false)
                .chatLimitBanToClubber(false)
                .kickToClubber(false)
                .banToClubber(false)
                .chatBan(true)
                .djWaitLock(false)
                .newDj(false)
                .musicSkip(false)
                .videoLengthLimit(false)
                .build();

        PartyPermission listener = PartyPermission
                .builder()
                .authority(PartyPermissionRole.LISTENER)
                .partyInfoFetch(false)
                .partyClose(false)
                .notice(false)
                .giveToClubber(false)
                .chatDelete(false)
                .chatLimitBanToClubber(false)
                .kickToClubber(false)
                .banToClubber(false)
                .chatBan(true)
                .djWaitLock(false)
                .newDj(false)
                .musicSkip(false)
                .videoLengthLimit(false)
                .build();

        partyPermissionRepository.save(admin);
        partyPermissionRepository.save(cm);
        partyPermissionRepository.save(mod);
        partyPermissionRepository.save(clubber);
        partyPermissionRepository.save(listener);

        Assertions.assertEquals(partyPermissionRepository.count(), 5);
    }

    @Test
    @Transactional
    void partyPermissionFindByAuthorityAndSetDto() {
        PartyPermission partyPermission = partyPermissionRepository.findByAuthority(PartyPermissionRole.ADMIN);
        PartyRoomPermissionDefaultDto partyRoomPermissionDefaultDto = om.mapper().convertValue(partyPermission, PartyRoomPermissionDefaultDto.class);

        Assertions.assertEquals(partyPermission.getAuthority(), partyRoomPermissionDefaultDto.getAuthority());
        Assertions.assertEquals(partyPermission.getPartyInfoFetch(), partyRoomPermissionDefaultDto.getPartyInfoFetch());
    }

}
