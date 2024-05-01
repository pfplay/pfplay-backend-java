package com.pfplaybackend.api.user.presentation.api;

import com.pfplaybackend.api.user.application.dto.AvatarBodyDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

public interface AvatarApi {
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
