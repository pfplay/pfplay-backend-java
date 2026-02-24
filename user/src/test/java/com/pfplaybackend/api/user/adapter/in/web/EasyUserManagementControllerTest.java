package com.pfplaybackend.api.user.adapter.in.web;

import com.pfplaybackend.api.common.config.security.jwt.CookieUtil;
import com.pfplaybackend.api.common.config.security.jwt.JwtService;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.user.application.service.initialize.TemporaryUserInitializeService;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EasyUserManagementController.class)
class EasyUserManagementControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean CookieUtil cookieUtil;
    @MockBean JwtService jwtService;
    @MockBean TemporaryUserInitializeService temporaryUserInitializeService;
    @MockBean JwtDecoder jwtDecoder;

    @Test
    @DisplayName("createAssociateMember — 200 OK")
    void createAssociateMemberReturns200() throws Exception {
        // given
        MemberData member = mock(MemberData.class);
        when(member.getUserId()).thenReturn(new UserId(1L));
        when(member.getEmail()).thenReturn("test@gmail.com");
        when(member.getAuthorityTier()).thenReturn(AuthorityTier.AM);
        when(temporaryUserInitializeService.addAssociateMember(any(UserId.class), anyString())).thenReturn(member);
        when(jwtService.generateNonExpiringAccessToken(any())).thenReturn("mock-token");

        // when & then
        mockMvc.perform(post("/api/v1/users/members/sign/temporary/associate-member")
                        .with(jwt())
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("createFullMember — 200 OK")
    void createFullMemberReturns200() throws Exception {
        // given
        MemberData member = mock(MemberData.class);
        when(member.getUserId()).thenReturn(new UserId(1L));
        when(member.getEmail()).thenReturn("test@gmail.com");
        when(member.getAuthorityTier()).thenReturn(AuthorityTier.AM);
        when(temporaryUserInitializeService.addAssociateMember(any(UserId.class), anyString())).thenReturn(member);
        when(temporaryUserInitializeService.upgradeMember(any(MemberData.class))).thenReturn(member);
        when(jwtService.generateNonExpiringAccessToken(any())).thenReturn("mock-token");

        // when & then
        mockMvc.perform(post("/api/v1/users/members/sign/temporary/full-member")
                        .with(jwt())
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}
