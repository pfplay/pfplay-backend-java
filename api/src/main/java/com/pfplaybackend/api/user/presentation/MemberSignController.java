package com.pfplaybackend.api.user.presentation;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.config.jwt.util.CookieUtil;
import com.pfplaybackend.api.user.application.service.MemberSignService;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Guest;
import com.pfplaybackend.api.user.presentation.payload.request.SignGuestRequest;
import com.pfplaybackend.api.user.presentation.payload.request.SignMemberRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Sign API")
@RequestMapping("/api/v1/users")
@Controller
@RequiredArgsConstructor
public class MemberSignController {

    final MemberSignService memberSignService;

    @GetMapping("/members/sign")
    public String memberSign(@ModelAttribute @Valid SignMemberRequest request) {
        return memberSignService.getOAuth2RedirectUri(request, "oauth-redirect");
    }
}