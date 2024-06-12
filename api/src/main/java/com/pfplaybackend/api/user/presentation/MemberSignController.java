package com.pfplaybackend.api.user.presentation;

import com.pfplaybackend.api.user.application.service.MemberSignService;
import com.pfplaybackend.api.user.presentation.payload.request.SignMemberRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberSignController {

    final MemberSignService memberSignService;

    @GetMapping("/sign")
    public String memberSign(@ModelAttribute @Valid SignMemberRequest request) {
        return memberSignService.getOAuth2RedirectUri(request);
    }
}