package com.pfplaybackend.api.user.adapter.in.web;

import com.pfplaybackend.api.common.config.security.jwt.CookieUtil;
import com.pfplaybackend.api.common.config.security.jwt.JwtService;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.user.application.service.UserWalletCommandService;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserWalletCommandController.class)
@Import(UserWalletCommandControllerTest.TestMethodSecurityConfig.class)
class UserWalletCommandControllerTest {

    @EnableMethodSecurity
    static class TestMethodSecurityConfig {}

    @Autowired MockMvc mockMvc;
    @MockBean UserWalletCommandService userWalletService;
    @MockBean CookieUtil cookieUtil;
    @MockBean JwtService jwtService;
    @MockBean JwtDecoder jwtDecoder;

    @Test
    @DisplayName("updateMyWallet — ROLE_MEMBER이면 200 OK")
    void updateMyWallet_member_returns200() throws Exception {
        // given
        MemberData member = mock(MemberData.class);
        when(member.getUserId()).thenReturn(new UserId(1L));
        when(member.getEmail()).thenReturn("test@gmail.com");
        when(member.getAuthorityTier()).thenReturn(AuthorityTier.FM);
        when(userWalletService.updateMyWalletAddress(any())).thenReturn(member);
        when(jwtService.generateNonExpiringAccessToken(any())).thenReturn("mock-token");

        String body = """
                {"walletAddress": "0x1234567890abcdef"}""";

        // when & then
        mockMvc.perform(put("/api/v1/users/me/profile/wallet")
                        .with(jwt().authorities(() -> "ROLE_MEMBER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("updateMyWallet — 인증 없으면 401")
    void updateMyWallet_unauthenticated_returns401() throws Exception {
        // given
        String body = """
                {"walletAddress": "0x1234567890abcdef"}""";

        // when & then
        mockMvc.perform(put("/api/v1/users/me/profile/wallet")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }
}
