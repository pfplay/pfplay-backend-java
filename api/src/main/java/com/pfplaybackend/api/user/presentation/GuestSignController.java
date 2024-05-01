package com.pfplaybackend.api.user.presentation;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.user.model.entity.Guest;
import com.pfplaybackend.api.user.presentation.dto.request.GuestCreateRequest;
import com.pfplaybackend.api.user.application.GuestSignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/guest")
public class GuestSignController {

    private final GuestSignService guestSignService;

    @GetMapping("/sign")
    public ResponseEntity<?> createGuest(
            @RequestBody GuestCreateRequest request
    ) {
        Guest guest = guestSignService.createGuest(request.getUserAgent());

//        GuestCreateResponse guestCreateResponse =
//                new GuestCreateResponse(
//                        guest.getId(),
//                        guest.getName(),
//                        false,
//                        Authority.ROLE_GUEST,
//                        guest.token
//                );

        return ResponseEntity.ok().body(ApiCommonResponse.success("OK"));
        // return ResponseEntity.ok().body(ApiCommonResponse.success(guestCreateResponse));
    }
}
