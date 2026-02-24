package com.pfplaybackend.api.common.config.security.jwt;

import com.pfplaybackend.api.common.config.security.jwt.properties.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class CookieUtilTest {

    private static final String SET_COOKIE_HEADER = "Set-Cookie";
    private static final String SECURE = "Secure";

    private CookieUtil cookieUtil;
    private JwtProperties jwtProperties;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
        jwtProperties.getCookie().setSecure(true);
        jwtProperties.getCookie().setSameSite("None");
        jwtProperties.getCookie().setPath("/");
        cookieUtil = new CookieUtil(jwtProperties);
    }

    @Test
    @DisplayName("deleteCookie - Set-Cookie 헤더에 SameSite, Secure 속성이 포함되어야 한다")
    void deleteCookieShouldIncludeSameSiteAndSecure() {
        // given
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        cookieUtil.deleteAccessTokenCookie(response);

        // then
        Collection<String> setCookieHeaders = response.getHeaders(SET_COOKIE_HEADER);
        assertThat(setCookieHeaders).isNotEmpty();

        String header = setCookieHeaders.iterator().next();
        assertThat(header).contains("Max-Age=0");
        assertThat(header).contains("SameSite=None");
        assertThat(header).contains(SECURE);
        assertThat(header).contains("HttpOnly");
        assertThat(header).contains("Path=/");
    }

    @Test
    @DisplayName("deleteCookie - addCookie와 동일한 속성 형식을 사용해야 한다")
    void deleteCookieShouldMatchAddCookieFormat() {
        // given
        MockHttpServletResponse addResponse = new MockHttpServletResponse();
        MockHttpServletResponse deleteResponse = new MockHttpServletResponse();

        // when
        cookieUtil.addAccessTokenCookie(addResponse, "test-token");
        cookieUtil.deleteAccessTokenCookie(deleteResponse);

        // then
        String addHeader = addResponse.getHeaders(SET_COOKIE_HEADER).iterator().next();
        String deleteHeader = deleteResponse.getHeaders(SET_COOKIE_HEADER).iterator().next();

        // 두 헤더 모두 SameSite 속성을 포함해야 한다
        assertThat(addHeader).contains("SameSite=");
        assertThat(deleteHeader).contains("SameSite=");

        // 두 헤더 모두 Secure 속성을 포함해야 한다
        assertThat(addHeader).contains(SECURE);
        assertThat(deleteHeader).contains(SECURE);
    }
}
