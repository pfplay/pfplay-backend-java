package com.pfplaybackend.api.user.application.service;

import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.user.domain.entity.data.AvatarBodyResourceData;
import com.pfplaybackend.api.user.domain.entity.data.ProfileData;
import com.pfplaybackend.api.user.domain.enums.ObtainmentType;
import com.pfplaybackend.api.user.domain.value.AvatarBodyUri;
import com.pfplaybackend.api.user.domain.value.AvatarFaceUri;
import com.pfplaybackend.api.user.domain.value.AvatarIconUri;
import com.pfplaybackend.api.user.application.dto.shared.AvatarFaceDto;
import com.pfplaybackend.api.user.application.dto.shared.AvatarIconDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileCommandServiceTest {

    @Mock UserAvatarQueryService userAvatarQueryService;
    @InjectMocks UserProfileCommandService userProfileCommandService;

    @Test
    @DisplayName("createProfileDataForGuest — 게스트 프로필이 기본 아바타로 생성된다")
    void createProfileDataForGuest_createsWithDefaultAvatar() {
        // given
        UserId userId = new UserId(1L);
        AvatarBodyResourceData defaultBody = AvatarBodyResourceData.builder()
                .id(1L).name("default").resourceUri("default-body")
                .obtainableType(ObtainmentType.BASIC).obtainableScore(0)
                .isCombinable(true).isDefaultSetting(true)
                .combinePositionX(50).combinePositionY(100)
                .build();
        when(userAvatarQueryService.getDefaultAvatarBodyResourceData()).thenReturn(defaultBody);
        when(userAvatarQueryService.getDefaultAvatarBodyUri()).thenReturn(new AvatarBodyUri("default-body"));
        when(userAvatarQueryService.getDefaultAvatarFaceUri()).thenReturn(new AvatarFaceUri("default-face"));
        when(userAvatarQueryService.getDefaultAvatarIconUri()).thenReturn(new AvatarIconUri("default-icon"));

        // when
        ProfileData profile = userProfileCommandService.createProfileDataForGuest(userId);

        // then
        assertThat(profile.getUserId()).isEqualTo(userId);
        assertThat(profile.getNicknameValue()).startsWith("Guest_");
        assertThat(profile.getAvatarSetting().getAvatarBodyUri().getAvatarBodyUri()).isEqualTo("default-body");
    }

    @Test
    @DisplayName("createProfileDataForMember — 멤버 프로필이 생성된다")
    void createProfileDataForMember_createsMinimalProfile() {
        // given
        UserId userId = new UserId(1L);

        // when
        ProfileData profile = userProfileCommandService.createProfileDataForMember(userId);

        // then
        assertThat(profile.getUserId()).isEqualTo(userId);
    }
}
