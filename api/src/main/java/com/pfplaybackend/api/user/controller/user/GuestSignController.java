package com.pfplaybackend.api.user.controller.user;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.common.enums.Authority;
import com.pfplaybackend.api.config.jwt.JwtProvider;
import com.pfplaybackend.api.config.mapper.ObjectMapperConfig;
import com.pfplaybackend.api.user.model.entity.user.Guest;
import com.pfplaybackend.api.user.presentation.user.request.GuestCreateRequest;
import com.pfplaybackend.api.user.presentation.user.response.GuestCreateResponse;
import com.pfplaybackend.api.user.service.user.GuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/guest")
public class GuestSignController {

    private final GuestService guestService;
    private final JwtProvider jwtProvider;
    private final ObjectMapperConfig om;

    @PostMapping("/sign")
    public ResponseEntity<?> createGuest(
            @RequestBody GuestCreateRequest request
    ) {
        Guest guest = guestService.createGuest(request.getUserAgent());
        String token = jwtProvider.generateAccessTokenForGuest(guest.getId());

        GuestCreateResponse guestCreateResponse =
                new GuestCreateResponse(
                        guest.getId(),
                        guest.getName(),
                        false,
                        Authority.ROLE_GUEST,
                        token
                );

        return ResponseEntity.ok().body(ApiCommonResponse.success(guestCreateResponse));
    }
}
