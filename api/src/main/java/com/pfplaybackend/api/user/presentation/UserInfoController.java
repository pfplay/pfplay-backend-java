package com.pfplaybackend.api.user.presentation;

import com.pfplaybackend.api.user.application.service.UserInfoService;
import com.pfplaybackend.api.user.presentation.payload.response.MyInfoResponse;
import com.pfplaybackend.api.user.domain.entity.domainmodel.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Info API", description = "Operations related to user's info management")
@RequestMapping("/api/v1/users")
@RestController
@RequiredArgsConstructor
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