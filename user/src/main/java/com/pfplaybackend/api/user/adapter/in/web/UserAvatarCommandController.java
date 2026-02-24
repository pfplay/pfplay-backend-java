package com.pfplaybackend.api.user.adapter.in.web;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.user.adapter.in.web.payload.request.UpdateAvatarRequest;
import com.pfplaybackend.api.user.application.dto.command.SetAvatarCommand;
import com.pfplaybackend.api.user.application.service.UserAvatarCommandService;
import com.pfplaybackend.api.user.application.validation.AvatarRequestValidator;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User Profile API")
@RequestMapping("/api/v1/users")
@RestController
@RequiredArgsConstructor
public class UserAvatarCommandController {

    private final UserAvatarCommandService userAvatarCommandService;
    private final AvatarRequestValidator avatarRequestValidator;

    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @PutMapping("/me/profile/avatar")
    public ResponseEntity<ApiCommonResponse<Void>> setMyAvatar(@Valid @RequestBody UpdateAvatarRequest request) {
        SetAvatarCommand command = toCommand(request);
        avatarRequestValidator.validate(command);
        userAvatarCommandService.setUserAvatar(command);
        return ResponseEntity.ok().body(ApiCommonResponse.ok());
    }

    private SetAvatarCommand toCommand(UpdateAvatarRequest request) {
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
