package com.pfplaybackend.api.user.adapter.in.web;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.common.config.security.enums.AccessLevel;
import com.pfplaybackend.api.common.config.security.jwt.CookieUtil;
import com.pfplaybackend.api.common.config.security.jwt.JwtService;
import com.pfplaybackend.api.common.config.security.jwt.dto.TokenClaimsRequest;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.user.application.service.GuestSignService;
import com.pfplaybackend.api.user.domain.entity.data.GuestData;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Sign API")
@RequestMapping("/api/v1/users")
@RestController
@RequiredArgsConstructor
public class GuestSignController {

    private final GuestSignService guestSignService;
    private final CookieUtil cookieUtil;
    private final JwtService jwtService;

    @PostMapping("/guests/sign")
    public ResponseEntity<?> createGuest(
            HttpServletResponse response
    ) {
        GuestData guest = guestSignService.getGuestOrCreate();
        cookieUtil.addAccessTokenCookie(response, jwtService.generateAccessToken(TokenClaimsRequest.builder()
                .uid(guest.getUserId().getUid().toString())
                .email("N/A")
                .accessLevel(AccessLevel.ROLE_GUEST)
                .authorityTier(AuthorityTier.GT)
                .build()));

        return ResponseEntity.ok()
                .body(ApiCommonResponse.success("OK"));
    }
}
