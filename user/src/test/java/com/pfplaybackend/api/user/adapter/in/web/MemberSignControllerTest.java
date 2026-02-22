package com.pfplaybackend.api.user.adapter.in.web;

import com.pfplaybackend.api.user.application.service.MemberSignService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberSignController.class)
class MemberSignControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean MemberSignService memberSignService;
    @MockBean JwtDecoder jwtDecoder;

    @Test
    @DisplayName("memberSign — 유효한 요청이면 3xx 리다이렉트를 반환한다")
    void memberSign_validRequest_returns3xx() throws Exception {
        // given
        when(memberSignService.getOAuth2RedirectUri(any(), eq("oauth-redirect")))
                .thenReturn("redirect:https://accounts.google.com/o/oauth2/auth");

        // when & then
        mockMvc.perform(get("/api/v1/users/members/sign")
                        .param("oauth2Provider", "GOOGLE")
                        .with(jwt())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("memberSign — oauth2Provider 없으면 400 Bad Request")
    void memberSign_missingProvider_returns400() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/users/members/sign")
                        .with(jwt())
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }
}
