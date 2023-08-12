package com.pfplaybackend.api.user.controller;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.entity.User;
import com.pfplaybackend.api.enums.ApiHeader;
import com.pfplaybackend.api.user.presentation.dto.DummyResponse;
import com.pfplaybackend.api.user.presentation.request.ProfileUpdateRequest;
import com.pfplaybackend.api.user.presentation.request.TokenRequest;
import com.pfplaybackend.api.user.presentation.response.UserInfoResponse;
import com.pfplaybackend.api.user.presentation.response.UserLoginSuccessResponse;
import com.pfplaybackend.api.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;


@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "user", description = "the user API")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@RestController
public class UserSignController {

    private final UserService userService;

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
            @RequestBody TokenRequest request, HttpServletResponse response
    ) {

        final String uri = "https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + request.getAccessToken();

        UserInfoResponse userInfoResponse;

        try {
            userInfoResponse = userService.request(uri, UserInfoResponse.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiCommonResponse.error(e.getMessage()));
        }

        String email = userInfoResponse.getEmail();
        if (Objects.isNull(email) || email.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiCommonResponse.error("email scope empty"));
        }

        Optional<User> findUser = Optional.ofNullable(userService.findByUser(email));

        String token;
        boolean registered = false;
        UserLoginSuccessResponse userLoginSuccessResponse;

        if (findUser.isEmpty()) {
            User user = userService.save(email);
            token = userService.registeredUserReturnJwt(user, user.getEmail());
            registered = true;
            userLoginSuccessResponse = new UserLoginSuccessResponse(
                    user.getId(),
                    null,
                    registered,
                    user.getAuthority()
            );
        } else {
            token = userService.registeredUserReturnJwt(findUser.orElseThrow(), email);
            userLoginSuccessResponse = new UserLoginSuccessResponse(
                    findUser.get().getId(),
                    findUser.get().getNickname(),
                    registered,
                    findUser.get().getAuthority()
            );
        }

        response.setHeader(ApiHeader.AUTHORIZATION.getValue(), ApiHeader.BEARER.getValue() + token);
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
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(new DummyResponse(jwtAuthenticationToken));
    }

    @PatchMapping("/profile")
    public ResponseEntity<?> userProfile(
            @RequestBody ProfileUpdateRequest request
    ) {
        try {
            JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            String email = jwtAuthenticationToken.getToken().getClaims().get("iss").toString();

            Optional<User> findUser = Optional.ofNullable(userService.findByUser(email));
            if (findUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("user not found");
            } else {
                userService.updateProfile(findUser.orElseThrow(), request);
                return ResponseEntity.ok().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
