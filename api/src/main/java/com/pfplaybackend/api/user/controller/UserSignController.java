package com.pfplaybackend.api.user.controller;

import com.pfplaybackend.api.common.ApiResponse;
import com.pfplaybackend.api.common.ResponseMessage;
import com.pfplaybackend.api.enums.Header;
import com.pfplaybackend.api.user.presentation.dto.DummyResponse;
import com.pfplaybackend.api.user.presentation.request.TokenRequest;
import com.pfplaybackend.api.user.presentation.response.UserInfoResponse;
import com.pfplaybackend.api.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@RestController
public class UserSignController {

    private final UserService userService;

    @PostMapping("/info")
    public ResponseEntity<?> userInfo(@RequestBody TokenRequest request, HttpServletResponse response) {

        final String uri = "https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + request.getAccessToken();

        UserInfoResponse userInfoResponse;

        try {
            userInfoResponse = userService.request(uri, UserInfoResponse.class);
        } catch (Exception e) {
            return ResponseEntity.ok().body(ApiResponse.error(ResponseMessage.make(HttpStatus.BAD_REQUEST.value(), e.getMessage())));
        }

        String accessToken = userService.makeJwt(userInfoResponse.getEmail());
        response.setHeader(Header.AUTHORIZATION.getValue(), Header.BEARER.getValue() + accessToken);
        return ResponseEntity.ok().body(ApiResponse.success(ResponseMessage.make(HttpStatus.OK.value(), HttpStatus.OK.name())));
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
