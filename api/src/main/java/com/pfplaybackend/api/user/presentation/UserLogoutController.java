package com.pfplaybackend.api.user.presentation;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.config.jwt.util.CookieUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Sign API")
@RequestMapping("/api/v1/users")
@RestController
@RequiredArgsConstructor
public class UserLogoutController {

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, CookieUtil.getExpiredCookie("AccessToken").toString());
        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiCommonResponse.success("OK"));
    }
}
