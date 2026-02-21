package com.pfplaybackend.api.user.adapter.in.web;

import com.pfplaybackend.api.user.application.validation.AvatarRequestValidator;
import com.pfplaybackend.api.user.adapter.in.web.payload.request.SetAvatarRequest;
import com.pfplaybackend.api.user.application.dto.command.SetAvatarCommand;
import com.pfplaybackend.api.user.application.dto.shared.AvatarBodyDto;
import com.pfplaybackend.api.user.application.dto.shared.AvatarFaceDto;
import com.pfplaybackend.api.user.application.service.UserAvatarService;
import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.user.adapter.in.web.api.UserAvatarApi;
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
    public ResponseEntity<ApiCommonResponse<List<AvatarBodyDto>>> getMyAllAvatarBodies() {
        List<AvatarBodyDto> avatarBodies = userAvatarService.findMyAvatarBodies();
        return ResponseEntity.ok().body(ApiCommonResponse.success(avatarBodies));
    }

    // TODO 1개 이상의 Face 가 제공될 수 있는지 여부에 따라서 확장(개선) 필요
    @PreAuthorize("hasAnyRole('ROLE_GUEST', 'ROLE_MEMBER')")
    @GetMapping("/me/profile/avatar/faces")
    public ResponseEntity<ApiCommonResponse<List<AvatarFaceDto>>> getMyDefaultAvatarFaces() {
        return ResponseEntity.ok().body(ApiCommonResponse.success(
                userAvatarService.findMyAvatarFaces())
        );
    }

    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @PutMapping("/me/profile/avatar")
    public ResponseEntity<ApiCommonResponse<Void>> setMyAvatar(@Valid @RequestBody SetAvatarRequest request) {
        SetAvatarCommand command = toCommand(request);
        avatarRequestValidator.validate(command);
        userAvatarService.setUserAvatar(command);
        return ResponseEntity.ok().body(ApiCommonResponse.ok());
    }

    private SetAvatarCommand toCommand(SetAvatarRequest request) {
        SetAvatarCommand.AvatarFaceSpec faceSpec = null;
        if (request.getFace() != null) {
            faceSpec = new SetAvatarCommand.AvatarFaceSpec(
                    request.getFace().getUri(),
                    request.getFace().getSourceType(),
                    new SetAvatarCommand.AvatarTransformSpec(
                            request.getFace().getTransform().getOffsetX(),
                            request.getFace().getTransform().getOffsetY(),
                            request.getFace().getTransform().getScale()
                    )
            );
        }
        return new SetAvatarCommand(
                request.getAvatarCompositionType(),
                new SetAvatarCommand.AvatarBodySpec(request.getBody().getUri()),
                faceSpec
        );
    }
}
