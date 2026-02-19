package com.pfplaybackend.api.user.adapter.in.web;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.common.config.security.enums.AccessLevel;
import com.pfplaybackend.api.common.config.security.jwt.CookieUtil;
import com.pfplaybackend.api.common.config.security.jwt.JwtService;
import com.pfplaybackend.api.common.config.security.jwt.dto.TokenClaimsRequest;
import com.pfplaybackend.api.user.application.dto.command.UpdateWalletCommand;
import com.pfplaybackend.api.user.application.service.UserWalletService;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import com.pfplaybackend.api.user.adapter.in.web.payload.request.UpdateMyWalletRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User Profile API")
@RequestMapping("/api/v1/users")
@RestController
@RequiredArgsConstructor
public class UserWalletController {

    private final UserWalletService userWalletService;
    private final CookieUtil cookieUtil;
    private final JwtService jwtService;

    /**
     * 호출한(인증된) 사용자의 프로필 리소스 내 Wallet 리소스를 갱신한다.
     * @param request
     * @return
     */
    @PutMapping("/me/profile/wallet")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity<?> updateMyWallet(@RequestBody UpdateMyWalletRequest request, HttpServletResponse response) {
        MemberData member = userWalletService.updateMyWalletAddress(UpdateWalletCommand.builder()
                .walletAddress(request.getWalletAddress())
                .build()
        );
        cookieUtil.addAccessTokenCookie(response, jwtService.generateNonExpiringAccessToken(TokenClaimsRequest.builder()
                .uid(member.getUserId().getUid().toString())
                .email(member.getEmail())
                .accessLevel(AccessLevel.ROLE_MEMBER)
                .authorityTier(member.getAuthorityTier())
                .build()));

        return ResponseEntity.ok()
                .body(ApiCommonResponse.success("OK"));
    }
}
