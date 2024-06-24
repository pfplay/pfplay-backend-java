package com.pfplaybackend.api.user.presentation;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.user.application.dto.shared.AvatarFaceDto;
import com.pfplaybackend.api.user.application.service.AvatarResourceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Avatar Resource API")
@RequestMapping("/api/v1/avatar-resources")
@RestController
@RequiredArgsConstructor
public class AvatarController {

    private final AvatarResourceService avatarResourceService;

    // TODO 1개 이상의 Face 가 제공될 수 있는지 여부에 따라서 확장(개선) 필요
    @PreAuthorize("hasAnyRole('ROLE_GUEST', 'ROLE_MEMBER')")
    @GetMapping("/faces")
    public ResponseEntity<?> getMyDefaultAvatarFaces() {
        return ResponseEntity.ok().body(ApiCommonResponse.success(
                avatarResourceService.getDefaultSettingFace())
        );
    }
}
