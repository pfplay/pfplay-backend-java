package com.pfplaybackend.api.user.adapter.in.web;

import com.pfplaybackend.api.common.config.security.jwt.CookieUtil;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.user.application.dto.result.MyInfoResult;
import com.pfplaybackend.api.user.application.service.UserInfoQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserInfoQueryController.class)
@Import(UserInfoQueryControllerTest.TestMethodSecurityConfig.class)
class UserInfoQueryControllerTest {

    @EnableMethodSecurity
    static class TestMethodSecurityConfig {}

    @Autowired MockMvc mockMvc;
    @MockBean UserInfoQueryService userInfoService;
    @MockBean CookieUtil cookieUtil;
    @MockBean JwtDecoder jwtDecoder;

    @Test
    @DisplayName("getMyInfo — ROLE_MEMBER이면 200 OK")
    void getMyInfoMemberReturns200() throws Exception {
        // given
        MyInfoResult result = new MyInfoResult("uid-123", "test@gmail.com", AuthorityTier.FM, true, LocalDate.of(2024, 1, 1));
        when(userInfoService.getMyInfo()).thenReturn(result);

        // when & then
        mockMvc.perform(get("/api/v1/users/me/info")
                        .with(jwt().authorities(() -> "ROLE_MEMBER")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("getMyInfo — ROLE_GUEST이면 200 OK")
    void getMyInfoGuestReturns200() throws Exception {
        // given
        MyInfoResult result = new MyInfoResult("uid-123", "test@gmail.com", AuthorityTier.FM, true, LocalDate.of(2024, 1, 1));
        when(userInfoService.getMyInfo()).thenReturn(result);

        // when & then
        mockMvc.perform(get("/api/v1/users/me/info")
                        .with(jwt().authorities(() -> "ROLE_GUEST")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("getMyInfo — 인증 없으면 401")
    void getMyInfoUnauthenticatedReturns401() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/users/me/info"))
                .andExpect(status().isUnauthorized());
    }
}
