package com.pfplaybackend.api.user.adapter.in.web;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.user.application.dto.result.MyInfoResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserInfoQueryControllerTest extends AbstractUserWebMvcTest {

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
