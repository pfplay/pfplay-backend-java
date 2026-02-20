package com.pfplaybackend.api.user.adapter.in.web;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.user.application.service.UserBioService;
import com.pfplaybackend.api.user.application.dto.command.UpdateBioCommand;
import com.pfplaybackend.api.user.application.service.UserProfileService;
import com.pfplaybackend.api.user.adapter.in.web.payload.request.UpdateMyBioRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User Profile API")
@RequestMapping("/api/v1/users")
@RestController
@RequiredArgsConstructor
public class UserBioController {

    private final UserBioService userBioService;

    /**
     * 호출한(인증된) 사용자의 프로필 리소스 내 Bio 리소스를 갱신한다.
     * @param request
     * @return
     */
    @PutMapping("/me/profile/bio")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity<?> setMyBio(@Valid @RequestBody UpdateMyBioRequest request) {
        UpdateBioCommand updateBioCommand = new UpdateBioCommand(request.getNickname(), request.getIntroduction());
        userBioService.updateMyBio(updateBioCommand);
        return ResponseEntity.ok().body(ApiCommonResponse.success("OK"));
    }
}
