package com.pfplaybackend.api.user.controller;

import com.pfplaybackend.api.common.ApiResponse;
import com.pfplaybackend.api.entity.User;
import com.pfplaybackend.api.enums.Authority;
import com.pfplaybackend.api.enums.Header;
import com.pfplaybackend.api.user.presentation.dto.DummyResponse;
import com.pfplaybackend.api.user.presentation.request.TokenRequest;
import com.pfplaybackend.api.user.presentation.response.UserInfoResponse;
import com.pfplaybackend.api.user.presentation.response.UserLoginSuccessResponse;
import com.pfplaybackend.api.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@RestController
public class UserSignController {

    private final UserService userService;

    @PostMapping("/info")
    public ResponseEntity<?> userInfo(
            @RequestBody TokenRequest request, HttpServletResponse response
    ) {

        final String uri = "https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + request.getAccessToken();

        UserInfoResponse userInfoResponse;

        try {
            userInfoResponse = userService.request(uri, UserInfoResponse.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }

        String email = userInfoResponse.getEmail();
        if(Objects.isNull(email) || email.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("email scope empty"));
        }

        Optional<User> findUser = Optional.ofNullable(userService.findByUser(email));

        String token;
        boolean registered = false;
        UserLoginSuccessResponse userLoginSuccessResponse;

        if(findUser.isEmpty()) {
            token = userService.notRegisteredUserReturnJwt(email);
            registered = true;
            userLoginSuccessResponse = new UserLoginSuccessResponse(registered, Authority.USER.getRole());
        } else {
            token = userService.registeredUserReturnJwt(findUser.orElseThrow(), email);
            userLoginSuccessResponse = new UserLoginSuccessResponse(registered, findUser.get().getAuthority().getRole());
        }

        response.setHeader(Header.AUTHORIZATION.getValue(), Header.BEARER.getValue() + token);
        return ResponseEntity.ok().body(ApiResponse.success(userLoginSuccessResponse));
    }

    @GetMapping("/dummy")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> dummy() {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(new DummyResponse(jwtAuthenticationToken));
    }

    @GetMapping("/jwt")
    @PreAuthorize("hasRole('USER_ADMIN')")
    public ResponseEntity<?> jwt() {
        return ResponseEntity.ok().build();
    }

}
