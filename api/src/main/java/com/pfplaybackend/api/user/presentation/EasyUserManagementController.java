package com.pfplaybackend.api.user.presentation;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.config.jwt.JwtProvider;
import com.pfplaybackend.api.config.jwt.util.CookieUtil;
import com.pfplaybackend.api.user.application.service.temporary.TemporaryUserService;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Member;
import com.pfplaybackend.api.user.domain.value.UserId;
import com.pfplaybackend.api.user.presentation.payload.request.SignGuestRequest;
import com.pfplaybackend.api.user.presentation.payload.request.SignMemberRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "User Sign API", description = "Operations related to user's sign management")
@Profile("dev")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class EasyUserManagementController {

    // RedirectUri after 'social login'
    @Value("${app.redirect.web.uri}")
    private String redirectWebUri;

    private final JwtProvider jwtProvider;
    private final TemporaryUserService temporaryUserService;

    @PostMapping("/sign/temporary/associate-member")
    public ResponseEntity<?> createAssociateMember() {

        UserId userId = new UserId(UUID.randomUUID());
        Member member = temporaryUserService.addAssociateMember(userId, userId.getUid().toString().substring(0,12) + "@gmail.com");
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, CookieUtil.getCookieWithToken("AccessToken",
                jwtProvider.generateAccessTokenForMember(member)).toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiCommonResponse.success("OK"));
    }

    @PostMapping("/sign/temporary/full-member")
    public ResponseEntity<?> createFullMember() {

        UserId userId = new UserId(UUID.randomUUID());
        Member member = temporaryUserService.upgradeMember(
                temporaryUserService.addAssociateMember(userId, userId.getUid().toString().substring(0,12) + "@gmail.com"));
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, CookieUtil.getCookieWithToken("AccessToken",
                jwtProvider.generateAccessTokenForMember(member)).toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiCommonResponse.success("OK"));
    }

    @PostMapping("/sign/temporary/redirect-uri/{redirectUri}")
    public ResponseEntity<?> configureRedirectUri(@PathVariable String redirectUri) {

        this.redirectWebUri = redirectUri;

        return ResponseEntity.ok()
                .body(ApiCommonResponse.success("OK"));
    }
}