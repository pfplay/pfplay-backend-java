package com.pfplaybackend.api.user.adapter.in.web;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.user.adapter.in.web.payload.request.UpdateMyBioRequest;
import com.pfplaybackend.api.user.application.dto.command.UpdateBioCommand;
import com.pfplaybackend.api.user.application.service.UserBioCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
public class UserBioCommandController {

    private final UserBioCommandService userBioService;

    @Operation(summary = "자기소개 수정", description = "현재 인증된 회원의 닉네임과 자기소개를 수정합니다. 회원만 사용 가능합니다.")
    @SecurityRequirement(name = "cookieAuth")
    @PutMapping("/me/profile/bio")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity<ApiCommonResponse<Void>> setMyBio(@Valid @RequestBody UpdateMyBioRequest request) {
        UpdateBioCommand updateBioCommand = new UpdateBioCommand(request.getNickname(), request.getIntroduction());
        userBioService.updateMyBio(updateBioCommand);
        return ResponseEntity.ok().body(ApiCommonResponse.ok());
    }
}
