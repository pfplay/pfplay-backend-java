package com.pfplaybackend.api.common.config.security.jwt;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

class CookieBearerTokenResolverTest {

    private CookieBearerTokenResolver resolver;

    @BeforeEach
    void setUp() throws Exception {
        resolver = new CookieBearerTokenResolver();
        Field field = CookieBearerTokenResolver.class.getDeclaredField("accessTokenCookieName");
        field.setAccessible(true);
        field.set(resolver, "access_token");
    }

    @Test
    @DisplayName("resolve — 쿠키에 토큰이 있으면 반환한다")
    void resolve_returnsTokenFromCookie() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("access_token", "test-jwt-token"));

        // when
        String result = resolver.resolve(request);

        // then
        assertThat(result).isEqualTo("test-jwt-token");
    }

    @Test
    @DisplayName("resolve — 쿠키에 토큰이 없으면 null을 반환한다")
    void resolve_returnsNullWhenNoCookies() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();

        // when
        String result = resolver.resolve(request);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("resolve — 다른 이름의 쿠키만 있으면 null을 반환한다")
    void resolve_returnsNullWhenWrongCookieName() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("other_cookie", "some-value"));

        // when
        String result = resolver.resolve(request);

        // then
        assertThat(result).isNull();
    }
}
