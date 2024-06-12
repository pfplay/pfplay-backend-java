package com.pfplaybackend.api.user.presentation;

import com.pfplaybackend.api.user.application.service.MemberSignService;
import com.pfplaybackend.api.user.presentation.payload.request.SignMemberRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "User Sign API", description = "Operations related to user's sign management")
@RequestMapping("/api/v1/members")
@Controller
@RequiredArgsConstructor
public class MemberSignController {

    final MemberSignService memberSignService;

    @GetMapping("/sign")
    public String memberSign(@ModelAttribute @Valid SignMemberRequest request) {
        return memberSignService.getOAuth2RedirectUri(request);
    }
}