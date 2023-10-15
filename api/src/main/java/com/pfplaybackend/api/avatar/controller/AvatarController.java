package com.pfplaybackend.api.avatar.controller;

import com.pfplaybackend.api.avatar.presentation.dto.AvatarBodyDto;
import com.pfplaybackend.api.avatar.presentation.response.AvatarBodyResponse;
import com.pfplaybackend.api.avatar.service.AvatarService;
import com.pfplaybackend.api.common.ApiCommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "avatar", description = "avatar api")
@RequestMapping("/api/v1/avatar")
@RestController
public class AvatarController {

    private AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @Operation(summary = "Avatar body list 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = AvatarBodyResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "500",
                    description = "조회 실패"
            )
    })
    @GetMapping("/body-list")
    public ResponseEntity<?> getAllAvatarBodies() {
        List<AvatarBodyDto> avatarBodyResponse = new ArrayList<AvatarBodyDto>(this.avatarService.getAvatarBodies());
        return ResponseEntity.ok().body(ApiCommonResponse.success(avatarBodyResponse));
    }
}
