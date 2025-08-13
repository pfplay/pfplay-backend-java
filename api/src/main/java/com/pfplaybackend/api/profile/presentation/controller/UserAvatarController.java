package com.pfplaybackend.api.profile.presentation.controller;

import com.pfplaybackend.api.profile.application.validation.AvatarRequestValidator;
import com.pfplaybackend.api.profile.presentation.dto.request.SetAvatarRequest;
import com.pfplaybackend.api.user.application.dto.command.UpdateAvatarFaceCommand;
import com.pfplaybackend.api.user.application.dto.shared.AvatarBodyDto;
import com.pfplaybackend.api.profile.application.service.UserAvatarService;
import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.user.presentation.api.UserAvatarApi;
import com.pfplaybackend.api.user.presentation.payload.request.UpdateMyAvatarFaceRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User Profile API")
@RequestMapping("/api/v1/users")
@RestController
@RequiredArgsConstructor
public class UserAvatarController implements UserAvatarApi {

    private final UserAvatarService userAvatarService;
    private final AvatarRequestValidator avatarRequestValidator;


    @PreAuthorize("hasAnyRole('ROLE_GUEST', 'ROLE_MEMBER')")
    @GetMapping("/me/profile/avatar/bodies")
    public ResponseEntity<?> getMyAllAvatarBodies() {
        List<AvatarBodyDto> avatarBodies = userAvatarService.findMyAvatarBodies();
        return ResponseEntity.ok().body(ApiCommonResponse.success(avatarBodies));
    }

    // TODO 1개 이상의 Face 가 제공될 수 있는지 여부에 따라서 확장(개선) 필요
    @PreAuthorize("hasAnyRole('ROLE_GUEST', 'ROLE_MEMBER')")
    @GetMapping("/me/profile/avatar/faces")
    public ResponseEntity<?> getMyDefaultAvatarFaces() {
        return ResponseEntity.ok().body(ApiCommonResponse.success(
                userAvatarService.findMyAvatarFaces())
        );
    }

    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @PutMapping("/me/profile/avatar")
    public ResponseEntity<?> setMyAvatar(@Valid @RequestBody SetAvatarRequest request) {
        avatarRequestValidator.validate(request);
        userAvatarService.setUserAvatar(request);
        return ResponseEntity.ok().body(ApiCommonResponse.success("OK"));
    }
}
