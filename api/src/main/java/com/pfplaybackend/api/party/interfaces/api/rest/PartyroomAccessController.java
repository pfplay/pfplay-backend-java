package com.pfplaybackend.api.party.interfaces.api.rest;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.common.config.security.jwt.CookieUtil;
import com.pfplaybackend.api.common.config.security.jwt.JwtService;
import com.pfplaybackend.api.party.application.service.PartyroomAccessService;
import com.pfplaybackend.api.party.domain.entity.domainmodel.Crew;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.interfaces.api.rest.payload.response.access.EnterPartyroomResponse;
import com.pfplaybackend.api.user.application.service.GuestSignService;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Guest;
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
        Crew crew = partyroomAccessService.tryEnter(new PartyroomId(partyroomId));
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
            Guest guest = guestSignService.getGuestOrCreate();
            cookieUtil.addAccessTokenCookie(response, jwtService.generateAccessTokenForGuest(guest));
        }
        return ResponseEntity.ok().body(ApiCommonResponse.success(partyroomAccessService.getRedirectUri(linkDomain)));
    }
}
