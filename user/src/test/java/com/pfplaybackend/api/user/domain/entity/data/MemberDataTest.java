package com.pfplaybackend.api.user.domain.entity.data;

import com.pfplaybackend.api.common.config.security.enums.ProviderType;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.user.domain.enums.ActivityType;
import com.pfplaybackend.api.user.domain.value.Nickname;
import com.pfplaybackend.api.user.domain.value.WalletAddress;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MemberDataTest {

    @Test
    @DisplayName("create — 팩토리 메서드로 생성 시 AM 권한 등급과 프로필 미업데이트 상태이다")
    void createDefaultState() {
        // when
        MemberData member = MemberData.create("test@email.com", ProviderType.GOOGLE);

        // then
        assertThat(member.getAuthorityTier()).isEqualTo(AuthorityTier.AM);
        assertThat(member.isProfileUpdated()).isFalse();
        assertThat(member.getEmail()).isEqualTo("test@email.com");
        assertThat(member.getProviderType()).isEqualTo(ProviderType.GOOGLE);
        assertThat(member.getUserId()).isNotNull();
    }

    @Test
    @DisplayName("createWithFixedUserId — 고정 UserId로 생성 시 동일한 UserId가 사용된다")
    void createWithFixedUserId() {
        // given
        UserId fixedId = new UserId(42L);

        // when
        MemberData member = MemberData.createWithFixedUserId(fixedId, "fixed@email.com", ProviderType.TWITTER);

        // then
        assertThat(member.getUserId()).isEqualTo(fixedId);
        assertThat(member.getEmail()).isEqualTo("fixed@email.com");
        assertThat(member.getAuthorityTier()).isEqualTo(AuthorityTier.AM);
    }

    @Test
    @DisplayName("updateProfileBio — 프로필 바이오 업데이트 시 isProfileUpdated가 true가 된다")
    void updateProfileBio() {
        // given
        MemberData member = MemberData.create("test@email.com", ProviderType.GOOGLE);
        ProfileData profile = ProfileData.builder()
                .userId(member.getUserId())
                .nickname(new Nickname("OldNick"))
                .build();
        member.initializeProfile(profile);

        // when
        member.updateProfileBio("NewNick", "Hello!");

        // then
        assertThat(member.isProfileUpdated()).isTrue();
    }

    @Test
    @DisplayName("updateWalletAddress — 지갑 주소 설정 시 권한이 FM으로 승격된다")
    void updateWalletAddressUpgradesAuthority() {
        // given
        MemberData member = MemberData.create("test@email.com", ProviderType.GOOGLE);
        ProfileData profile = ProfileData.builder()
                .userId(member.getUserId())
                .nickname(new Nickname("Nick"))
                .build();
        member.initializeProfile(profile);

        // when
        member.updateWalletAddress(new WalletAddress("0x1234567890abcdef"));

        // then
        assertThat(member.getAuthorityTier()).isEqualTo(AuthorityTier.FM);
    }

    @Test
    @DisplayName("updateDjScore — DJ 점수 업데이트 시 ActivityData의 score가 변경된다")
    void updateDjScore() {
        // given
        MemberData member = MemberData.create("test@email.com", ProviderType.GOOGLE);
        Map<ActivityType, ActivityData> activityMap = new HashMap<>();
        activityMap.put(ActivityType.DJ_PNT, ActivityData.create(member.getUserId(), ActivityType.DJ_PNT, 10));
        member.initializeActivityMap(activityMap);

        // when
        member.updateDjScore(5);

        // then
        assertThat(member.getActivityDataMap().get(ActivityType.DJ_PNT).getScore().getValue()).isEqualTo(15);
    }

    @Test
    @DisplayName("isGuest — Member는 항상 false를 반환한다")
    void isGuestReturnsFalse() {
        // given
        MemberData member = MemberData.create("test@email.com", ProviderType.GOOGLE);

        // then
        assertThat(member.isGuest()).isFalse();
    }
}
