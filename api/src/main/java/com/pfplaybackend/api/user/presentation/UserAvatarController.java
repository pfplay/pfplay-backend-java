package com.pfplaybackend.api.user.presentation;

import com.pfplaybackend.api.user.application.dto.command.UpdateAvatarBodyCommand;
import com.pfplaybackend.api.user.application.dto.command.UpdateAvatarFaceCommand;
import com.pfplaybackend.api.user.application.dto.shared.AvatarBodyDto;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSummaryDto;
import com.pfplaybackend.api.user.application.service.UserAvatarService;
import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.user.application.service.UserProfileService;
import com.pfplaybackend.api.user.presentation.payload.request.UpdateMyAvatarBodyRequest;
import com.pfplaybackend.api.user.presentation.payload.request.UpdateMyAvatarFaceRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "avatar", description = "avatar api")
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@RestController
public class UserAvatarController {

    private final UserAvatarService userAvatarService;

    @PreAuthorize("hasAnyRole('ROLE_GUEST', 'ROLE_MEMBER')")
    @GetMapping("/me/profile/avatar/bodies")
    public ResponseEntity<?> getMyAllAvatarBodies() {
        List<AvatarBodyDto> avatarBodies = userAvatarService.findMyAvatarBodies();
        return ResponseEntity.ok().body(ApiCommonResponse.success(avatarBodies));
    }

    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @PutMapping("/me/profile/avatar/body")
    public ResponseEntity<?> updateMyAvatarBody(@RequestBody UpdateMyAvatarBodyRequest updateMyAvatarBodyRequest) {
        UpdateAvatarBodyCommand updateAvatarBodyCommand = new UpdateAvatarBodyCommand(updateMyAvatarBodyRequest.getAvatarBodyUri());
        userAvatarService.updateAvatarBodyUri(updateAvatarBodyCommand);
        return ResponseEntity.ok().body(ApiCommonResponse.success("OK"));
    }

    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @PutMapping("/me/profile/avatar/face")
    public ResponseEntity<?> updateMyAvatarFace(@RequestBody UpdateMyAvatarFaceRequest updateMyAvatarFaceRequest) {
        UpdateAvatarFaceCommand updateAvatarFaceCommand = new UpdateAvatarFaceCommand(updateMyAvatarFaceRequest.getAvatarFaceUri());
        userAvatarService.updateAvatarFaceUri(updateAvatarFaceCommand);
        return ResponseEntity.ok().body(ApiCommonResponse.success("OK"));
    }
}
