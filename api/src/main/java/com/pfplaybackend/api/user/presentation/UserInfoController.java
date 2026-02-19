package com.pfplaybackend.api.user.presentation;

import com.pfplaybackend.api.user.application.service.UserInfoService;
import com.pfplaybackend.api.user.presentation.payload.response.MyInfoResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Sign API")
@RequestMapping("/api/v1/users")
@RestController
@RequiredArgsConstructor
public class UserInfoController {

    final private UserInfoService userInfoService;

    @GetMapping("/me/info")
    @PreAuthorize("hasAnyRole('GUEST', 'MEMBER')")
    public ResponseEntity<MyInfoResponse> getMyInfo() {
        MyInfoResponse response = userInfoService.getMyInfo();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
