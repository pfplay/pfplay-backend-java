package com.pfplaybackend.api.user.presentation;

import com.pfplaybackend.api.config.oauth2.dto.CustomAuthentication;
import com.pfplaybackend.api.user.application.AvatarService;
import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.user.presentation.api.ProfileApi;
import com.pfplaybackend.api.user.presentation.dto.request.ProfileUpdateRequest;
import com.pfplaybackend.api.user.presentation.dto.response.UserProfileResponse;
import com.pfplaybackend.api.user.application.UserInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class ProfileController implements ProfileApi {

    private final UserInfoService userInfoService;
    private final AvatarService avatarService;

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(CustomAuthentication authentication) {
        try {
            // Member member = userInfoService.findByUser(authentication.getEmail()).orElseThrow();

            // AvatarBodyDto avatarBodyDto = avatarService.getAvatarBody(member.getBodyId());
            UserProfileResponse response = UserProfileResponse.builder()
//                    .nickname(member.getNickname())
//                    .introduction(member.getIntroduction())
//                    .faceUrl(member.getFaceUrl())
//                    .bodyId(member.getBodyId())
//                    .bodyUrl(avatarBodyDto.getImage())
//                    .walletAddress(member.getWalletAddress())
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
        // Member member = userInfoService.findByUser(authentication.getEmail()).orElseThrow();

        // userInfoService.setProfile(member, request);
        return ResponseEntity.ok().body(ApiCommonResponse.success("OK"));
    }

    @Override
    public ResponseEntity<?> getUserProfile() {
        return null;
    }
}
