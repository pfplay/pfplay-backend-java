package com.pfplaybackend.api.user.controller.profile.api;

import com.pfplaybackend.api.user.presentation.profile.dto.AvatarBodyDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

public interface UserAvatarApi {
    @Operation(summary = "Avatar body list 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = AvatarBodyDto.class))
                    )
            )
    })
    public ResponseEntity<?> getAllAvatarBodies();
}
