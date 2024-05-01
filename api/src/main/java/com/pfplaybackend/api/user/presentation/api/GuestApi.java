package com.pfplaybackend.api.user.presentation.api;

import com.pfplaybackend.api.user.presentation.dto.request.GuestCreateRequest;
import com.pfplaybackend.api.user.presentation.dto.response.GuestCreateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface GuestApi {

    @Operation(summary = "게스트 생성")
    @ApiResponses(value = {
            @ApiResponse(description = "게스트 생성",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GuestCreateResponse.class)
                    )
            )
    })
    public ResponseEntity<?> createGuest(
            @RequestBody GuestCreateRequest request
    );
}
