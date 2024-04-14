package com.pfplaybackend.api.user.controller.profile;

import com.pfplaybackend.api.config.oauth2.dto.CustomAuthentication;
import com.pfplaybackend.api.user.presentation.profile.dto.AvatarBodyDto;
import com.pfplaybackend.api.user.service.profile.AvatarService;
import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.user.controller.profile.api.UserProfileApi;
import com.pfplaybackend.api.user.model.entity.user.User;
import com.pfplaybackend.api.user.presentation.user.request.ProfileUpdateRequest;
import com.pfplaybackend.api.user.presentation.user.response.UserProfileResponse;
import com.pfplaybackend.api.user.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserProfileController implements UserProfileApi {

    private final UserService userService;
    private final AvatarService avatarService;

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        try {
            CustomAuthentication authentication = (CustomAuthentication) SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findByUser(authentication.getEmail()).orElseThrow();

            AvatarBodyDto avatarBodyDto = avatarService.getAvatarBody(user.getBodyId());
            UserProfileResponse response = UserProfileResponse.builder()
                    .nickname(user.getNickname())
                    .introduction(user.getIntroduction())
                    .faceUrl(user.getFaceUrl())
                    .bodyId(user.getBodyId())
                    .bodyUrl(avatarBodyDto.getImage())
                    .walletAddress(user.getWalletAddress())
                    .build();
            return ResponseEntity.ok().body(ApiCommonResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @Secured("ROLE_USER")
    @PatchMapping("/profile")
    public ResponseEntity<?> setUserProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        CustomAuthentication authentication = (CustomAuthentication) SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUser(authentication.getEmail()).orElseThrow();

        userService.setProfile(user, request);
        return ResponseEntity.ok().body(ApiCommonResponse.success("OK"));
    }
}
