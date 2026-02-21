package com.pfplaybackend.api.admin.application.service;

import com.pfplaybackend.api.admin.application.port.out.AdminMemberPort;
import com.pfplaybackend.api.admin.application.port.out.AdminPlaylistPort;
import com.pfplaybackend.api.common.config.security.enums.ProviderType;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.exception.http.ForbiddenException;
import com.pfplaybackend.api.common.exception.http.NotFoundException;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import com.pfplaybackend.api.user.domain.entity.data.ProfileData;
import com.pfplaybackend.api.user.domain.enums.ActivityType;
import com.pfplaybackend.api.user.domain.entity.data.ActivityData;
import com.pfplaybackend.api.user.domain.value.AvatarBodyUri;
import com.pfplaybackend.api.user.domain.value.AvatarFaceUri;
import com.pfplaybackend.api.user.domain.value.Nickname;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    @Mock
    private AdminMemberPort adminMemberPort;

    @Mock
    private AdminProfileService adminProfileService;

    @Mock
    private AdminPlaylistPort adminPlaylistPort;

    @InjectMocks
    private AdminUserService adminUserService;

    private MemberData createVirtualMemberData(UserId userId) {
        return MemberData.builder()
                .userId(userId)
                .email("virtual_test@pfplay.system")
                .authorityTier(AuthorityTier.AM)
                .providerType(ProviderType.ADMIN)
                .isProfileUpdated(false)
                .build();
    }

    private MemberData createGoogleMemberData(UserId userId) {
        return MemberData.builder()
                .userId(userId)
                .email("user@gmail.com")
                .authorityTier(AuthorityTier.AM)
                .providerType(ProviderType.GOOGLE)
                .isProfileUpdated(false)
                .build();
    }

    private ProfileData createDummyProfile(UserId userId) {
        return ProfileData.builder()
                .userId(userId)
                .nickname(new Nickname("Virtual_AABBCC"))
                .introduction("")
                .build();
    }

    @Test
    @DisplayName("createVirtualMember — 성공 시 ADMIN 프로바이더로 생성되고 FM으로 업그레이드된다")
    void createVirtualMember_success() {
        // given
        ProfileData dummyProfile = createDummyProfile(new UserId(1L));
        Map<ActivityType, ActivityData> activityMap = new HashMap<>();

        when(adminMemberPort.findMemberByEmail(anyString())).thenReturn(Optional.empty());
        when(adminProfileService.createProfileForVirtualMember(any(UserId.class), any(), any(), any()))
                .thenReturn(dummyProfile);
        when(adminMemberPort.createUserActivities(any(UserId.class))).thenReturn(activityMap);
        when(adminMemberPort.saveMember(any(MemberData.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        MemberData result = adminUserService.createVirtualMember();

        // then
        assertThat(result.getProviderType()).isEqualTo(ProviderType.ADMIN);
        assertThat(result.getAuthorityTier()).isEqualTo(AuthorityTier.FM);
        verify(adminPlaylistPort).createDefaultPlaylist(any(UserId.class));
        verify(adminMemberPort, times(2)).saveMember(any(MemberData.class));
    }

    @Test
    @DisplayName("createVirtualMember — 생성된 이메일은 virtual_*@pfplay.system 형식이다")
    void createVirtualMember_emailFormat() {
        // given
        ProfileData dummyProfile = createDummyProfile(new UserId(1L));
        Map<ActivityType, ActivityData> activityMap = new HashMap<>();

        when(adminMemberPort.findMemberByEmail(anyString())).thenReturn(Optional.empty());
        when(adminProfileService.createProfileForVirtualMember(any(UserId.class), any(), any(), any()))
                .thenReturn(dummyProfile);
        when(adminMemberPort.createUserActivities(any(UserId.class))).thenReturn(activityMap);
        when(adminMemberPort.saveMember(any(MemberData.class))).thenAnswer(invocation -> {
            MemberData member = invocation.getArgument(0);
            return member;
        });

        // when
        MemberData result = adminUserService.createVirtualMember();

        // then
        assertThat(result.getEmail()).startsWith("virtual_");
        assertThat(result.getEmail()).endsWith("@pfplay.system");
    }

    @Test
    @DisplayName("updateVirtualMemberAvatar — 가상 회원의 아바타 업데이트가 성공한다")
    void updateVirtualMemberAvatar_success() {
        // given
        UserId userId = new UserId(200L);
        MemberData virtualMember = createVirtualMemberData(userId);
        ProfileData existingProfile = createDummyProfile(userId);
        virtualMember.initializeProfile(existingProfile);

        AvatarBodyUri newBodyUri = new AvatarBodyUri("new_body.png");
        AvatarFaceUri newFaceUri = new AvatarFaceUri("new_face.png");
        ProfileData updatedProfile = createDummyProfile(userId);

        when(adminMemberPort.findMemberById(userId.getUid())).thenReturn(Optional.of(virtualMember));
        when(adminProfileService.createProfileForVirtualMember(
                eq(userId), eq("Virtual_AABBCC"), eq(newBodyUri), eq(newFaceUri)))
                .thenReturn(updatedProfile);
        when(adminMemberPort.saveMember(any(MemberData.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        MemberData result = adminUserService.updateVirtualMemberAvatar(userId, newBodyUri, newFaceUri);

        // then
        assertThat(result.getProfileData()).isEqualTo(updatedProfile);
        verify(adminMemberPort).saveMember(virtualMember);
    }

    @Test
    @DisplayName("updateVirtualMemberAvatar — 비가상 회원 아바타 업데이트 시 ForbiddenException이 발생한다")
    void updateVirtualMemberAvatar_nonVirtual_throws() {
        // given
        UserId userId = new UserId(201L);
        MemberData googleMember = createGoogleMemberData(userId);

        when(adminMemberPort.findMemberById(userId.getUid())).thenReturn(Optional.of(googleMember));

        // when & then
        assertThatThrownBy(() -> adminUserService.updateVirtualMemberAvatar(
                userId, new AvatarBodyUri("body.png"), new AvatarFaceUri("face.png")))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("deleteVirtualMember — 가상 회원 삭제가 성공한다")
    void deleteVirtualMember_success() {
        // given
        UserId userId = new UserId(300L);
        MemberData virtualMember = createVirtualMemberData(userId);

        when(adminMemberPort.findMemberById(userId.getUid())).thenReturn(Optional.of(virtualMember));

        // when
        adminUserService.deleteVirtualMember(userId);

        // then
        verify(adminMemberPort).deleteMemberById(userId.getUid());
    }

    @Test
    @DisplayName("deleteVirtualMember — 비가상 회원 삭제 시 ForbiddenException이 발생한다")
    void deleteVirtualMember_nonVirtual_throws() {
        // given
        UserId userId = new UserId(301L);
        MemberData googleMember = createGoogleMemberData(userId);

        when(adminMemberPort.findMemberById(userId.getUid())).thenReturn(Optional.of(googleMember));

        // when & then
        assertThatThrownBy(() -> adminUserService.deleteVirtualMember(userId))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("getVirtualMember — 존재하지 않는 userId로 조회 시 NotFoundException이 발생한다")
    void getVirtualMember_notFound_throws() {
        // given
        UserId userId = new UserId(999L);

        when(adminMemberPort.findMemberById(userId.getUid())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminUserService.getVirtualMember(userId))
                .isInstanceOf(NotFoundException.class);
    }
}
