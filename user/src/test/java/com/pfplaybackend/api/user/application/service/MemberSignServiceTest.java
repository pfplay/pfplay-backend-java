package com.pfplaybackend.api.user.application.service;

import com.pfplaybackend.api.common.config.security.enums.ProviderType;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.user.application.dto.command.SignMemberCommand;
import com.pfplaybackend.api.user.application.port.out.OAuth2RedirectPort;
import com.pfplaybackend.api.user.application.port.out.PlaylistSetupPort;
import com.pfplaybackend.api.user.domain.entity.data.ActivityData;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import com.pfplaybackend.api.user.domain.entity.data.ProfileData;
import com.pfplaybackend.api.user.domain.enums.ActivityType;
import com.pfplaybackend.api.user.adapter.out.persistence.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberSignServiceTest {

    @Mock OAuth2RedirectPort oauth2RedirectPort;
    @Mock MemberRepository memberRepository;
    @Mock UserProfileService userProfileService;
    @Mock UserActivityService userActivityService;
    @Mock PlaylistSetupPort playlistSetupPort;
    @InjectMocks MemberSignService memberSignService;

    @Test
    @DisplayName("getMemberOrCreate — 기존 회원이 있으면 그대로 반환한다")
    void getMemberOrCreate_existingMember() {
        // given
        String email = "test@example.com";
        MemberData existing = mock(MemberData.class);
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(existing));

        // when
        MemberData result = memberSignService.getMemberOrCreate(email, ProviderType.GOOGLE);

        // then
        assertThat(result).isSameAs(existing);
        verify(memberRepository, never()).save(any());
        verify(playlistSetupPort, never()).createDefaultPlaylist(any());
    }

    @Test
    @DisplayName("getMemberOrCreate — 신규 회원이면 프로필, 활동, 플레이리스트를 초기화하고 저장한다")
    void getMemberOrCreate_newMember() {
        // given
        String email = "new@example.com";
        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        ProfileData profile = mock(ProfileData.class);
        when(userProfileService.createProfileDataForMember(any(UserId.class))).thenReturn(profile);

        Map<ActivityType, ActivityData> activityMap = Map.of();
        when(userActivityService.createUserActivities(any(UserId.class))).thenReturn(activityMap);

        when(memberRepository.save(any(MemberData.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        MemberData result = memberSignService.getMemberOrCreate(email, ProviderType.GOOGLE);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
        verify(userProfileService).createProfileDataForMember(any(UserId.class));
        verify(userActivityService).createUserActivities(any(UserId.class));
        verify(playlistSetupPort).createDefaultPlaylist(any(UserId.class));
        verify(memberRepository).save(any(MemberData.class));
    }

    @Test
    @DisplayName("getOAuth2RedirectUri — OAuth2 리다이렉트 URI를 반환한다")
    void getOAuth2RedirectUri_success() {
        // given
        String expectedUri = "https://accounts.google.com/o/oauth2/v2/auth?...";
        when(oauth2RedirectPort.getRedirectUri("google", "/callback")).thenReturn(expectedUri);

        SignMemberCommand command = new SignMemberCommand("google");

        // when
        String result = memberSignService.getOAuth2RedirectUri(command, "/callback");

        // then
        assertThat(result).isEqualTo(expectedUri);
        verify(oauth2RedirectPort).getRedirectUri("google", "/callback");
    }
}
