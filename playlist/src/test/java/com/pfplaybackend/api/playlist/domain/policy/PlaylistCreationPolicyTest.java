package com.pfplaybackend.api.playlist.domain.policy;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.exception.http.ConflictException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlaylistCreationPolicyTest {

    @Test
    @DisplayName("FM 회원 — 10개 미만이면 생성 가능")
    void fmUnderLimit() {
        PlaylistCreationPolicy policy = new PlaylistCreationPolicy();
        assertThatNoException().isThrownBy(() -> policy.enforce(AuthorityTier.FM, 9));
    }

    @Test
    @DisplayName("FM 회원 — 10개에 도달하면 생성 불가")
    void fmAtLimit() {
        PlaylistCreationPolicy policy = new PlaylistCreationPolicy();
        assertThatThrownBy(() -> policy.enforce(AuthorityTier.FM, 10))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    @DisplayName("AM 회원 — 1개 미만이면 생성 가능")
    void amUnderLimit() {
        PlaylistCreationPolicy policy = new PlaylistCreationPolicy();
        assertThatNoException().isThrownBy(() -> policy.enforce(AuthorityTier.AM, 0));
    }

    @Test
    @DisplayName("AM 회원 — 1개에 도달하면 생성 불가")
    void amAtLimit() {
        PlaylistCreationPolicy policy = new PlaylistCreationPolicy();
        assertThatThrownBy(() -> policy.enforce(AuthorityTier.AM, 1))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    @DisplayName("GT 게스트도 AM과 동일한 제약")
    void guestLimit() {
        PlaylistCreationPolicy policy = new PlaylistCreationPolicy();
        assertThatThrownBy(() -> policy.enforce(AuthorityTier.GT, 1))
                .isInstanceOf(ConflictException.class);
    }
}
