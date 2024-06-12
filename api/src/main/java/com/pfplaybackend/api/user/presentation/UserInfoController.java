package com.pfplaybackend.api.user.presentation;

import com.pfplaybackend.api.user.application.service.UserInfoService;
import com.pfplaybackend.api.user.presentation.payload.response.MyInfoResponse;
import com.pfplaybackend.api.user.domain.model.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserInfoController {

    final private UserInfoService userInfoService;

    @GetMapping("/me/info")
    @PreAuthorize("hasAnyRole('ROLE_GUEST', 'ROLE_MEMBER')")
    public ResponseEntity<MyInfoResponse> getMyInfo() {
        User user = userInfoService.getMyInfo();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(MyInfoResponse.builder()
                        .uid(user.getUserId().getUid().toString())
                        .isProfileUpdated(user.isProfileUpdated())
                        .registrationDate(user.getCreatedAt().toLocalDate())
                        .authorityTier(user.getAuthorityTier())
                        .build());
    }
}