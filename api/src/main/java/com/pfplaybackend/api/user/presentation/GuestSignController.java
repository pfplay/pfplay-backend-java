package com.pfplaybackend.api.user.presentation;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.config.jwt.JwtProvider;
import com.pfplaybackend.api.config.jwt.util.CookieUtil;
import com.pfplaybackend.api.user.application.service.GuestSignService;
import com.pfplaybackend.api.user.domain.model.domain.Guest;
import com.pfplaybackend.api.user.presentation.payload.request.SignGuestRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/guests")
public class GuestSignController {

    private final GuestSignService guestSignService;
    private final JwtProvider jwtProvider;

    @PostMapping("/sign")
    public ResponseEntity<?> createGuest(
            @RequestBody SignGuestRequest request
    ) {
        Guest guest = guestSignService.getGuestOrCreate(request.getUserAgent());
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, CookieUtil.getCookieWithToken("AccessToken",
                        jwtProvider.generateAccessTokenForGuest(guest)).toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiCommonResponse.success("OK"));
    }
}
