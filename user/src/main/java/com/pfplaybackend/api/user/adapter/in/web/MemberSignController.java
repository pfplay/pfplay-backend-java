package com.pfplaybackend.api.user.adapter.in.web;

import com.pfplaybackend.api.user.adapter.in.web.payload.request.MemberSignRequest;
import com.pfplaybackend.api.user.application.dto.command.SignMemberCommand;
import com.pfplaybackend.api.user.application.service.MemberSignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "User Sign API")
@RequestMapping("/api/v1/users")
@Controller
@RequiredArgsConstructor
public class MemberSignController {

    final MemberSignService memberSignService;

    @Operation(summary = "회원 가입/로그인", description = "OAuth2 제공자(Google, Twitter)를 통한 회원 가입 또는 로그인을 처리합니다. OAuth2 인증 페이지로 리다이렉트됩니다.")
    @GetMapping("/members/sign")
    public String memberSign(
            @Parameter(description = "OAuth2 제공자 정보 (oauth2Provider: GOOGLE 또는 TWITTER)")
            @ModelAttribute @Valid MemberSignRequest request) {
        SignMemberCommand command = new SignMemberCommand(request.getOauth2Provider());
        return memberSignService.getOAuth2RedirectUri(command, "oauth-redirect");
    }
}
