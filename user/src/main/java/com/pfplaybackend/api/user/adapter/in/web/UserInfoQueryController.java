package com.pfplaybackend.api.user.adapter.in.web;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.common.config.security.jwt.CookieUtil;
import com.pfplaybackend.api.common.exception.http.UnauthorizedException;
import com.pfplaybackend.api.user.application.service.UserInfoQueryService;
import com.pfplaybackend.api.user.application.dto.result.MyInfoResult;
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
public class UserInfoQueryController {

    private final UserInfoQueryService userInfoService;
    private final CookieUtil cookieUtil;

    @GetMapping("/me/info")
    @PreAuthorize("hasAnyRole('GUEST', 'MEMBER')")
    public ResponseEntity<ApiCommonResponse<MyInfoResult>> getMyInfo(HttpServletResponse response) {
        try {
            MyInfoResult myInfoResult = userInfoService.getMyInfo();
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiCommonResponse.success(myInfoResult));
        } catch (UnauthorizedException e) {
            cookieUtil.deleteAccessTokenCookie(response);
            cookieUtil.deleteRefreshTokenCookie(response);
            throw e;
        }
    }
}
