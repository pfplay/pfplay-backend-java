package com.pfplaybackend.api.user.presentation;

import com.pfplaybackend.api.config.oauth2.dto.CustomAuthentication;
import com.pfplaybackend.api.user.application.UserInfoService;
import com.pfplaybackend.api.user.presentation.api.AvatarApi;
import com.pfplaybackend.api.user.application.AvatarService;
import com.pfplaybackend.api.common.ApiCommonResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "avatar", description = "avatar api")
@RequiredArgsConstructor
@RequestMapping("/api/v1/avatar")
@RestController
public class AvatarController implements AvatarApi {

    private final AvatarService avatarService;
    private final UserInfoService userInfoService;

    @GetMapping("/body-list")
    public ResponseEntity<?> getAllAvatarBodies() {
        CustomAuthentication authentication = (CustomAuthentication) SecurityContextHolder.getContext().getAuthentication();
        // Member member = userInfoService.findByUser(authentication.getEmail()).orElseThrow();

        // List<AvatarBodyDto> response = this.avatarService.getAvatarBodies(member.getId());
        // return ResponseEntity.ok().body(ApiCommonResponse.success(response));
        return ResponseEntity.ok().body(ApiCommonResponse.success("OK"));
    }
}
