package com.pfplaybackend.api.user.adapter.in.web;

import com.pfplaybackend.api.common.config.security.jwt.CookieUtil;
import com.pfplaybackend.api.common.config.security.jwt.JwtService;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.user.application.service.GuestSignService;
import com.pfplaybackend.api.user.domain.entity.data.GuestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GuestSignController.class)
class GuestSignControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean GuestSignService guestSignService;
    @MockBean CookieUtil cookieUtil;
    @MockBean JwtService jwtService;
    @MockBean JwtDecoder jwtDecoder;

    @Test
    @DisplayName("createGuest — 200 OK + 쿠키 설정")
    void createGuestReturns200() throws Exception {
        // given
        GuestData guest = GuestData.createWithFixedUserId(new UserId(1L), "test-agent");
        when(guestSignService.getGuestOrCreate()).thenReturn(guest);
        when(jwtService.generateAccessToken(any())).thenReturn("mock-jwt-token");

        // when & then
        mockMvc.perform(post("/api/v1/users/guests/sign")
                        .with(jwt())
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}
