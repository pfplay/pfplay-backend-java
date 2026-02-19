package com.pfplaybackend.api.party.adapter.in.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PartyroomAccessControllerLinkTest {

    @Test
    @DisplayName("anonymousUser는 비인증 사용자로 판별되어야 한다")
    void anonymousUser_shouldBeIdentifiedAsUnauthenticated() {
        // given
        Authentication auth = new AnonymousAuthenticationToken(
                "key", "anonymousUser",
                List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));

        // when
        boolean isUnauthenticated = !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal());

        // then
        assertThat(isUnauthenticated).isTrue();
    }

    @Test
    @DisplayName("인증된 사용자는 정상 인증으로 판별되어야 한다")
    void authenticatedUser_shouldBeIdentifiedAsAuthenticated() {
        // given
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "user-id", null,
                List.of(new SimpleGrantedAuthority("ROLE_MEMBER")));

        // when
        boolean isUnauthenticated = !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal());

        // then
        assertThat(isUnauthenticated).isFalse();
    }
}
