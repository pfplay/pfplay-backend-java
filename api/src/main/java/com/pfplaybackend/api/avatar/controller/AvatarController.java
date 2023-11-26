package com.pfplaybackend.api.avatar.controller;

import com.pfplaybackend.api.avatar.presentation.dto.AvatarBodyDto;
import com.pfplaybackend.api.avatar.presentation.response.AvatarBodyResponse;
import com.pfplaybackend.api.avatar.service.AvatarService;
import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.common.JwtTokenInfo;
import com.pfplaybackend.api.entity.User;
import com.pfplaybackend.api.user.service.CustomUserDetailService;
import com.pfplaybackend.api.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;

@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "avatar", description = "avatar api")
@RequiredArgsConstructor
@RequestMapping("/api/v1/avatar")
@RestController
public class AvatarController {

    private final AvatarService avatarService;

    private final UserService userService;
    private final CustomUserDetailService customUserDetailService;

    @Operation(summary = "Avatar body list 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = AvatarBodyResponse.class)
                    )
            )
    })
    @GetMapping("/body-list")
    public ResponseEntity<?> getAllAvatarBodies() {
        JwtTokenInfo jwtTokenInfo = customUserDetailService.getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        User user = Optional.of(userService.findByUser(jwtTokenInfo.getEmail()))
                .orElseThrow(NoSuchElementException::new);
        List<AvatarBodyDto> response = this.avatarService.getAvatarBodies(user.getId());
        return ResponseEntity.ok().body(ApiCommonResponse.success(response));
    }
}
