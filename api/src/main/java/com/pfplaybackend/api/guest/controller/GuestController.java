package com.pfplaybackend.api.guest.controller;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.config.TokenProvider;
import com.pfplaybackend.api.entity.Guest;
import com.pfplaybackend.api.enums.ApiHeader;
import com.pfplaybackend.api.enums.Authority;
import com.pfplaybackend.api.guest.presentation.request.GuestCreateRequest;
import com.pfplaybackend.api.guest.presentation.response.GuestCreateResponse;
import com.pfplaybackend.api.guest.service.GuestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/guest")
public class GuestController {

    private final GuestService guestService;
    private final TokenProvider tokenProvider;

    @Operation(summary = "게스트")
    @ApiResponses(value = {
            @ApiResponse(description = "게스트",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GuestCreateResponse.class)
                    )
            )
    })
    @PostMapping("/create")
    public ResponseEntity<?> createGuest(
            @RequestBody GuestCreateRequest request,
            HttpServletResponse response
    ) {

        Guest guest = guestService.createGuest(request.getUserAgent());
        GuestCreateResponse guestCreateResponse =
                new GuestCreateResponse(guest.getId(), guest.getName(), false, Authority.GUEST.getRole());

        String token = tokenProvider.createGuestAccessToken(Authority.GUEST, guest.getId());
        response.setHeader(ApiHeader.AUTHORIZATION.getValue(), ApiHeader.BEARER.getValue() + token);

        return ResponseEntity.ok().body(ApiCommonResponse.success(guestCreateResponse));
    }

}
