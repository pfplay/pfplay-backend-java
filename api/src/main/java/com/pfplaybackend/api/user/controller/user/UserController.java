package com.pfplaybackend.api.user.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    @GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_GUEST')")
    public String getCurrentUser() {
        return "현재 사용자 정보를 반환합니다.";
    }
}