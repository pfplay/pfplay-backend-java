package com.pfplaybackend.api.user.adapter.in.web;

import com.pfplaybackend.api.user.application.service.MemberSignService;
import com.pfplaybackend.api.user.adapter.in.web.payload.request.MemberSignRequest;
import com.pfplaybackend.api.user.application.dto.command.SignMemberCommand;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Sign API")
@RequestMapping("/api/v1/users")
@Controller
@RequiredArgsConstructor
public class MemberSignController {

    final MemberSignService memberSignService;

    @GetMapping("/members/sign")
    public String memberSign(@ModelAttribute @Valid MemberSignRequest request) {
        SignMemberCommand command = new SignMemberCommand(request.getOauth2Provider());
        return memberSignService.getOAuth2RedirectUri(command, "oauth-redirect");
    }
}
