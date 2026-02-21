package com.pfplaybackend.api.user.adapter.in.web;

import com.pfplaybackend.api.user.application.dto.shared.AvatarBodyDto;
import com.pfplaybackend.api.user.application.dto.shared.AvatarFaceDto;
import com.pfplaybackend.api.user.application.service.UserAvatarQueryService;
import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.user.adapter.in.web.api.UserAvatarApi;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User Profile API")
@RequestMapping("/api/v1/users")
@RestController
@RequiredArgsConstructor
public class UserAvatarQueryController implements UserAvatarApi {

    private final UserAvatarQueryService userAvatarQueryService;

    @PreAuthorize("hasAnyRole('ROLE_GUEST', 'ROLE_MEMBER')")
    @GetMapping("/me/profile/avatar/bodies")
    public ResponseEntity<ApiCommonResponse<List<AvatarBodyDto>>> getMyAllAvatarBodies() {
        List<AvatarBodyDto> avatarBodies = userAvatarQueryService.findMyAvatarBodies();
        return ResponseEntity.ok().body(ApiCommonResponse.success(avatarBodies));
    }

    // TODO 1개 이상의 Face 가 제공될 수 있는지 여부에 따라서 확장(개선) 필요
    @PreAuthorize("hasAnyRole('ROLE_GUEST', 'ROLE_MEMBER')")
    @GetMapping("/me/profile/avatar/faces")
    public ResponseEntity<ApiCommonResponse<List<AvatarFaceDto>>> getMyDefaultAvatarFaces() {
        return ResponseEntity.ok().body(ApiCommonResponse.success(
                userAvatarQueryService.findMyAvatarFaces())
        );
    }
}
