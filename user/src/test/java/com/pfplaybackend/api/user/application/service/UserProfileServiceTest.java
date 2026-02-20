package com.pfplaybackend.api.user.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.common.domain.enums.AvatarCompositionType;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.user.adapter.out.persistence.GuestRepository;
import com.pfplaybackend.api.user.adapter.out.persistence.MemberRepository;
import com.pfplaybackend.api.user.adapter.out.persistence.UserProfileRepository;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSummaryDto;
import com.pfplaybackend.api.user.domain.entity.data.GuestData;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import com.pfplaybackend.api.user.domain.entity.data.ProfileData;
import com.pfplaybackend.api.user.domain.enums.FaceSourceType;
import com.pfplaybackend.api.user.domain.value.*;
import com.pfplaybackend.api.common.config.security.enums.ProviderType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock UserProfileRepository userProfileRepository;
    @Mock GuestRepository guestRepository;
    @Mock MemberRepository memberRepository;
    @Mock UserAvatarService userAvatarService;

    @InjectMocks UserProfileService userProfileService;

    private final UserId userId = new UserId(1L);

    @BeforeEach
    void setUp() {
        AuthContext authContext = mock(AuthContext.class);
        lenient().when(authContext.getUserId()).thenReturn(userId);
        lenient().when(authContext.getAuthorityTier()).thenReturn(AuthorityTier.FM);
        ThreadLocalContext.setContext(authContext);
    }

    @AfterEach
    void tearDown() {
        ThreadLocalContext.clearContext();
    }

    private ProfileData createProfileData(UserId uid, String nickname) {
        return ProfileData.builder()
                .userId(uid)
                .nickname(new Nickname(nickname))
                .avatarCompositionType(AvatarCompositionType.BODY_WITH_FACE)
                .faceSourceType(FaceSourceType.INTERNAL_IMAGE)
                .avatarBodyUri(new AvatarBodyUri("body_uri"))
                .avatarFaceUri(new AvatarFaceUri("face_uri"))
                .avatarIconUri(new AvatarIconUri("icon_uri"))
                .walletAddress(new WalletAddress(""))
                .build();
    }

    // ========== getUsersProfileSetting ==========

    @Test
    @DisplayName("getUsersProfileSetting — 다수 사용자의 프로필 설정 정보를 일괄 조회한다")
    void getUsersProfileSetting_multipleUsers() {
        // given
        UserId user2 = new UserId(2L);
        ProfileData profile1 = createProfileData(userId, "User1");
        ProfileData profile2 = createProfileData(user2, "User2");

        when(userProfileRepository.findAllByUserIdIn(List.of(userId, user2)))
                .thenReturn(List.of(profile1, profile2));

        // when
        Map<UserId, ProfileSettingDto> result = userProfileService.getUsersProfileSetting(List.of(userId, user2));

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(userId).nickname()).isEqualTo("User1");
        assertThat(result.get(user2).nickname()).isEqualTo("User2");
    }

    @Test
    @DisplayName("getUsersProfileSetting — 빈 사용자 목록에 대해 빈 맵을 반환한다")
    void getUsersProfileSetting_emptyList() {
        // given
        when(userProfileRepository.findAllByUserIdIn(List.of())).thenReturn(List.of());

        // when
        Map<UserId, ProfileSettingDto> result = userProfileService.getUsersProfileSetting(List.of());

        // then
        assertThat(result).isEmpty();
    }

    // ========== getMyProfileSummary ==========

    @Test
    @DisplayName("getMyProfileSummary — Member 사용자의 프로필 요약을 반환한다")
    void getMyProfileSummary_member() {
        // given
        MemberData member = MemberData.createWithFixedUserId(userId, "test@email.com", ProviderType.GOOGLE);
        ProfileData profile = createProfileData(userId, "MemberNick");
        member.initializeProfile(profile);
        member.initializeActivityMap(Map.of());

        when(memberRepository.findByUserId(userId)).thenReturn(Optional.of(member));

        // when
        ProfileSummaryDto summary = userProfileService.getMyProfileSummary();

        // then
        assertThat(summary).isNotNull();
        assertThat(summary.nickname()).isEqualTo("MemberNick");
    }

    @Test
    @DisplayName("getMyProfileSummary — Guest 사용자의 프로필 요약을 반환한다")
    void getMyProfileSummary_guest() {
        // given — Guest 경로를 타도록 AuthorityTier.GT 설정
        AuthContext guestContext = mock(AuthContext.class);
        when(guestContext.getUserId()).thenReturn(userId);
        when(guestContext.getAuthorityTier()).thenReturn(AuthorityTier.GT);
        ThreadLocalContext.setContext(guestContext);

        GuestData guest = GuestData.createWithFixedUserId(userId, "test-agent");
        ProfileData profile = createProfileData(userId, "GuestNick");
        guest.initiateProfile(profile);

        when(guestRepository.findByUserId(userId)).thenReturn(Optional.of(guest));

        // when
        ProfileSummaryDto summary = userProfileService.getMyProfileSummary();

        // then
        assertThat(summary).isNotNull();
        assertThat(summary.nickname()).isEqualTo("GuestNick");
    }
}
