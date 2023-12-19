package com.pfplaybackend.api.user.controller;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.config.ObjectMapperConfig;
import com.pfplaybackend.api.config.TokenProvider;
import com.pfplaybackend.api.entity.Guest;
import com.pfplaybackend.api.common.enums.Authority;
import com.pfplaybackend.api.user.presentation.request.GuestCreateRequest;
import com.pfplaybackend.api.user.presentation.response.GuestCreateResponse;
import com.pfplaybackend.api.user.service.GuestService;
import com.pfplaybackend.api.user.presentation.dto.UserPermissionDto;
import com.pfplaybackend.api.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final ObjectMapperConfig om;

    @Operation(summary = "게스트 생성")
    @ApiResponses(value = {
            @ApiResponse(description = "게스트 생성",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GuestCreateResponse.class)
                    )
            )
    })
    @PostMapping("/create")
    public ResponseEntity<?> createGuest(
            @RequestBody GuestCreateRequest request
    ) {

        Guest guest = guestService.createGuest(request.getUserAgent());
        String token = tokenProvider.createGuestAccessToken(Authority.ROLE_GUEST, guest.getId());
        UserPermissionDto userPermissionDto = om.mapper().convertValue(userService.getUserPermission(Authority.ROLE_GUEST), UserPermissionDto.class);
        GuestCreateResponse guestCreateResponse =
                new GuestCreateResponse(
                        guest.getId(),
                        guest.getName(),
                        false,
                        Authority.ROLE_GUEST,
                        token,
                        userPermissionDto
                );

        return ResponseEntity.ok().body(ApiCommonResponse.success(guestCreateResponse));
    }

}
