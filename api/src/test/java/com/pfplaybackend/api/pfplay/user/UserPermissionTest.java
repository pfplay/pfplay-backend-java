package com.pfplaybackend.api.pfplay.user;

import com.pfplaybackend.api.config.ObjectMapperConfig;
import com.pfplaybackend.api.entity.UserPermission;
import com.pfplaybackend.api.enums.Authority;
import com.pfplaybackend.api.user.presentation.dto.UserPermissionDto;
import com.pfplaybackend.api.user.repository.PermissionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
class UserPermissionTest {

    @Autowired
    PermissionRepository permissionRepository;

    @Autowired
    ObjectMapperConfig om;

    @Test
    @Transactional
    @Rollback(value = false)
    void createPermission() {
        UserPermission guest = UserPermission.builder()
                .authority(Authority.ROLE_GUEST)
                .settingProfile(false)
                .showPartyListDisplay(false)
                .enterMainStage(true)
                .chat(true)
                .createPlayList(false)
                .createWaitDj(false)
                .enterPartyRoom(true)
                .createPartyRoom(false)
                .admin(false)
                .communityManager(false)
                .moderator(false)
                .clubber(false)
                .listener(true)
                .build();

        UserPermission google = UserPermission.builder()
                .authority(Authority.ROLE_USER)
                .settingProfile(true)
                .showPartyListDisplay(true)
                .enterMainStage(true)
                .chat(true)
                .createPlayList(true)
                .createWaitDj(true)
                .enterPartyRoom(true)
                .createPartyRoom(false)
                .admin(false)
                .communityManager(true)
                .moderator(true)
                .clubber(true)
                .listener(true)
                .build();

        UserPermission wallet = UserPermission.builder()
                .authority(Authority.ROLE_WALLET_USER)
                .settingProfile(true)
                .showPartyListDisplay(true)
                .enterMainStage(true)
                .chat(true)
                .createPlayList(true)
                .createWaitDj(true)
                .enterPartyRoom(true)
                .createPartyRoom(true)
                .admin(true)
                .communityManager(true)
                .moderator(true)
                .clubber(true)
                .listener(true)
                .build();

        permissionRepository.save(guest);
        permissionRepository.save(google);
        permissionRepository.save(wallet);
    }

    @Test
    void findAllByAuthority() {
        UserPermission entity = permissionRepository.findAllByAuthority(Authority.ROLE_USER);
        UserPermissionDto userPermissionDto = om.mapper().convertValue(entity, UserPermissionDto.class);

        Assertions.assertTrue(userPermissionDto.getChat());
        Assertions.assertTrue(userPermissionDto.getCreatePlayList());
        Assertions.assertFalse(userPermissionDto.getCreatePartyRoom());
    }
}