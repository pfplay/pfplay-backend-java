package com.pfplaybackend.api.user.presentation;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.common.config.security.jwt.CookieUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Sign API")
@RequestMapping("/api/v1/users")
@RestController
@RequiredArgsConstructor
public class UserLogoutController {

    private final CookieUtil cookieUtil;

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        HttpHeaders headers = new HttpHeaders();
        cookieUtil.deleteAccessTokenCookie(response);

        return ResponseEntity.ok()
                .body(ApiCommonResponse.success("OK"));
    }
}
