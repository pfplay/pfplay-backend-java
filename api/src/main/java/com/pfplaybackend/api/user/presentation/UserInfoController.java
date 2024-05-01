package com.pfplaybackend.api.user.presentation;

import com.pfplaybackend.api.config.jwt.dto.UserAuthenticationDto;
import com.pfplaybackend.api.user.application.UserInfoService;
import com.pfplaybackend.api.user.model.domain.MemberDomain;
import com.pfplaybackend.api.user.model.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserInfoController {

    final private UserInfoService userInfoService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_GUEST')")
    public void getCurrentUser(Authentication authentication) {
        UserAuthenticationDto userAuthenticationDto = (UserAuthenticationDto) authentication.getPrincipal();
        System.out.println(userAuthenticationDto.getUserId().getUid());
        System.out.println(UserId.create(userAuthenticationDto.getUserId().getUid()));

        MemberDomain memberDomain = userInfoService.findByUserId(userAuthenticationDto.getUserId());
        System.out.println(memberDomain);
    }
}