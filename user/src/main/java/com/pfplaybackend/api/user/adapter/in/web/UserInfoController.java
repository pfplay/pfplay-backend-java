package com.pfplaybackend.api.user.adapter.in.web;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.common.config.security.jwt.CookieUtil;
import com.pfplaybackend.api.common.exception.http.UnauthorizedException;
import com.pfplaybackend.api.user.application.service.UserInfoService;
import com.pfplaybackend.api.user.adapter.in.web.payload.response.MyInfoResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Sign API")
@RequestMapping("/api/v1/users")
@RestController
@RequiredArgsConstructor
public class UserInfoController {

    private final UserInfoService userInfoService;
    private final CookieUtil cookieUtil;

    @GetMapping("/me/info")
    @PreAuthorize("hasAnyRole('GUEST', 'MEMBER')")
    public ResponseEntity<ApiCommonResponse<MyInfoResponse>> getMyInfo(HttpServletResponse response) {
        try {
            MyInfoResponse myInfoResponse = userInfoService.getMyInfo();
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiCommonResponse.success(myInfoResponse));
        } catch (UnauthorizedException e) {
            cookieUtil.deleteAccessTokenCookie(response);
            cookieUtil.deleteRefreshTokenCookie(response);
            throw e;
        }
    }
}
