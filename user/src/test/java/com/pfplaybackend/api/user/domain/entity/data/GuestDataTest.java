package com.pfplaybackend.api.user.domain.entity.data;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.domain.value.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GuestDataTest {

    @Test
    @DisplayName("create — GT 권한으로 생성된다")
    void createSetsGuestAuthority() {
        // when
        GuestData guest = GuestData.create();

        // then
        assertThat(guest.getAuthorityTier()).isEqualTo(AuthorityTier.GT);
        assertThat(guest.getUserId()).isNotNull();
        assertThat(guest.isProfileUpdated()).isFalse();
    }

    @Test
    @DisplayName("createWithFixedUserId — 지정된 UserId로 생성된다")
    void createWithFixedUserIdSetsGivenUserIdAndAgent() {
        // given
        UserId fixedId = new UserId(999L);

        // when
        GuestData guest = GuestData.createWithFixedUserId(fixedId, "test-agent");

        // then
        assertThat(guest.getUserId()).isEqualTo(fixedId);
        assertThat(guest.getAgent()).isEqualTo("test-agent");
        assertThat(guest.getAuthorityTier()).isEqualTo(AuthorityTier.GT);
    }

    @Test
    @DisplayName("initiateProfile — 프로필이 설정되고 isProfileUpdated가 true가 된다")
    void initiateProfileSetsProfileAndFlag() {
        // given
        GuestData guest = GuestData.create();
        ProfileData profile = ProfileData.builder()
                .userId(guest.getUserId())
                .build();

        // when
        guest.initiateProfile(profile);

        // then
        assertThat(guest.getProfileData()).isEqualTo(profile);
        assertThat(guest.isProfileUpdated()).isTrue();
    }

    @Test
    @DisplayName("isGuest — true를 반환한다")
    void isGuestReturnsTrue() {
        // when
        GuestData guest = GuestData.create();

        // then
        assertThat(guest.isGuest()).isTrue();
    }
}
