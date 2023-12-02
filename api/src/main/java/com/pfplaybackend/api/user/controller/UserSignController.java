package com.pfplaybackend.api.user.controller;

import com.pfplaybackend.api.avatar.presentation.dto.AvatarBodyDto;
import com.pfplaybackend.api.avatar.service.AvatarService;
import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.common.JwtTokenInfo;
import com.pfplaybackend.api.config.ObjectMapperConfig;
import com.pfplaybackend.api.entity.User;
import com.pfplaybackend.api.user.presentation.dto.DummyResponse;
import com.pfplaybackend.api.user.presentation.request.ProfileUpdateRequest;
import com.pfplaybackend.api.user.presentation.request.TokenRequest;
import com.pfplaybackend.api.user.presentation.response.UserInfoResponse;
import com.pfplaybackend.api.user.presentation.response.UserLoginSuccessResponse;
import com.pfplaybackend.api.user.presentation.response.UserProfileResponse;
import com.pfplaybackend.api.user.service.CustomUserDetailService;
import com.pfplaybackend.api.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;



@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "user", description = "user api")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@RestController
public class UserSignController {

    private final UserService userService;
    private final CustomUserDetailService customUserDetailService;
    private final AvatarService avatarService;

    @Operation(summary = "유저 회원가입 및 로그인")
    @ApiResponses(value = {
            @ApiResponse(description = "유저 회원가입 및 로그인",
                    content = @Content(
                        schema = @Schema(implementation = UserLoginSuccessResponse.class)
                    )
            )
    })
    @PostMapping("/info")
    public ResponseEntity<?> userInfo(
            @RequestBody TokenRequest request
    ) {
        final String uri = "https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + request.getAccessToken();
        UserInfoResponse googleToken = userService.request(uri, UserInfoResponse.class);
        UserLoginSuccessResponse userLoginSuccessResponse = userService.register(googleToken);
        return ResponseEntity.ok().body(ApiCommonResponse.success(userLoginSuccessResponse));
    }

    @ApiResponses(value = {
            @ApiResponse(description = "jwt 유저 인증 테스트. 게스트 jwt 접근 불가능",
                    content = @Content(
                        schema = @Schema(implementation = UserLoginSuccessResponse.class)
                    )
            )
    })
    @GetMapping("/jwt-auth-dummy")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> dummy() {
        JwtTokenInfo userDetails = customUserDetailService.getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.ok(new DummyResponse(userDetails));
    }

    @Operation(summary = "유저 마이 프로필 설정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "프로필 설정 성공"
            ),
            @ApiResponse(responseCode = "500",
                    description = "프로필 설정 실패"
            )
    })
    @Secured("ROLE_USER")
    @PatchMapping("/profile")
    public ResponseEntity<?> updateUserProfile(
            @Valid @RequestBody ProfileUpdateRequest request
    ) {
        try {
            JwtTokenInfo user = customUserDetailService.getUserDetails(SecurityContextHolder.getContext().getAuthentication());
            userService.updateProfile(user.getUser(), request);
            return ResponseEntity.ok().body(ApiCommonResponse.success("OK"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @Operation(summary = "유저 마이 프로필 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "프로필 조회 성공"
            ),
            @ApiResponse(responseCode = "500",
                    description = "프로필 조회 실패"
            )
    })
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        try {
            JwtTokenInfo jwtTokenInfo = customUserDetailService.getUserDetails(SecurityContextHolder.getContext().getAuthentication());
            User user = jwtTokenInfo.getUser();
            AvatarBodyDto avatarBodyDto = avatarService.getAvatarBody(user.getBodyId());
            UserProfileResponse response = UserProfileResponse.builder()
                    .nickname(user.getNickname())
                    .introduction(user.getIntroduction())
                    .faceUrl(user.getFaceUrl())
                    .bodyId(user.getBodyId())
                    .bodyUrl(avatarBodyDto.getImage())
                    .walletAddress(user.getWalletAddress())
                    .build();
            return ResponseEntity.ok().body(ApiCommonResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
