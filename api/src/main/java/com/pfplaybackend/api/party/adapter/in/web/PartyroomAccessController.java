package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.common.config.security.enums.AccessLevel;
import com.pfplaybackend.api.common.config.security.jwt.CookieUtil;
import com.pfplaybackend.api.common.config.security.jwt.JwtService;
import com.pfplaybackend.api.common.config.security.jwt.dto.TokenClaimsRequest;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.party.application.service.PartyroomAccessService;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.adapter.in.web.payload.response.access.EnterPartyroomResponse;
import com.pfplaybackend.api.user.application.service.GuestSignService;
import com.pfplaybackend.api.user.domain.entity.data.GuestData;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Partyroom API")
@RequestMapping("/api/v1/partyrooms")
@RestController
@RequiredArgsConstructor
public class PartyroomAccessController {

    private final PartyroomAccessService partyroomAccessService;
    private final GuestSignService guestSignService;
    private final CookieUtil cookieUtil;
    private final JwtService jwtService;

    /**
     *
     * @param partyroomId
     * @return
     */
    @PostMapping("/{partyroomId}/enter")
    public ResponseEntity<?> enterPartyroom(
            @PathVariable Long partyroomId) {
        CrewData crew = partyroomAccessService.tryEnter(new PartyroomId(partyroomId));
        return ResponseEntity.ok().body(ApiCommonResponse.success(EnterPartyroomResponse.from(crew)));
    }

    @PostMapping("/{partyroomId}/exit")
    public ResponseEntity<?> exitPartyroom(
            @PathVariable Long partyroomId) {
        partyroomAccessService.exit(new PartyroomId(partyroomId));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/link/{linkDomain}/enter")
    public ResponseEntity<?> enterPartyroomByLinkAddress(
            @PathVariable String linkDomain,
            HttpServletResponse response) {
        // 비인증 사용자인 경우 게스트 토큰 자동 발급
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            GuestData guest = guestSignService.getGuestOrCreate();
            cookieUtil.addAccessTokenCookie(response, jwtService.generateAccessToken(TokenClaimsRequest.builder()
                    .uid(guest.getUserId().getUid().toString())
                    .email("N/A")
                    .accessLevel(AccessLevel.ROLE_GUEST)
                    .authorityTier(AuthorityTier.GT)
                    .build()));
        }
        return ResponseEntity.ok().body(ApiCommonResponse.success(partyroomAccessService.getRedirectUri(linkDomain)));
    }
}
