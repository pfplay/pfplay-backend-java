package com.pfplaybackend.api.user.presentation;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.common.config.security.jwt.CookieUtil;
import com.pfplaybackend.api.common.config.security.jwt.JwtService;
import com.pfplaybackend.api.user.application.service.initialize.TemporaryUserInitializeService;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Member;
import com.pfplaybackend.api.user.domain.value.UserId;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Sign API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class EasyUserManagementController {

    private final CookieUtil cookieUtil;
    private final JwtService jwtService;

    private final TemporaryUserInitializeService temporaryUserInitializeService;

    @PostMapping("/members/sign/temporary/associate-member")
    public ResponseEntity<?> createAssociateMember(HttpServletResponse response) {
        UserId userId = new UserId();
        Member member = temporaryUserInitializeService.addAssociateMember(userId, userId.getUid().toString().substring(0,12) + "@gmail.com");
        cookieUtil.addAccessTokenCookie(response, jwtService.generateNonExpiringAccessTokenForMember(member));

        return ResponseEntity.ok()
                .body(ApiCommonResponse.success("OK"));
    }

    @PostMapping("/members/sign/temporary/full-member")
    public ResponseEntity<?> createFullMember(HttpServletResponse response) {
        UserId userId = new UserId();
        Member member = temporaryUserInitializeService.upgradeMember(
                temporaryUserInitializeService.addAssociateMember(userId, userId.getUid().toString().substring(0,12) + "@gmail.com"));
        cookieUtil.addAccessTokenCookie(response, jwtService.generateNonExpiringAccessTokenForMember(member));

        return ResponseEntity.ok()
                .body(ApiCommonResponse.success("OK"));
    }
}