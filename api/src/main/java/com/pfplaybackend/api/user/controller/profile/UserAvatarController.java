package com.pfplaybackend.api.user.controller.profile;

import com.pfplaybackend.api.config.oauth2.dto.CustomAuthentication;
import com.pfplaybackend.api.user.controller.profile.api.UserAvatarApi;
import com.pfplaybackend.api.user.presentation.profile.dto.AvatarBodyDto;
import com.pfplaybackend.api.user.service.profile.AvatarService;
import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.user.model.entity.user.User;
import com.pfplaybackend.api.user.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;

@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "avatar", description = "avatar api")
@RequiredArgsConstructor
@RequestMapping("/api/v1/avatar")
@RestController
public class UserAvatarController implements UserAvatarApi {

    private final AvatarService avatarService;
    private final UserService userService;

    @GetMapping("/body-list")
    public ResponseEntity<?> getAllAvatarBodies() {
        CustomAuthentication authentication = (CustomAuthentication) SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUser(authentication.getEmail()).orElseThrow();

        List<AvatarBodyDto> response = this.avatarService.getAvatarBodies(user.getId());
        return ResponseEntity.ok().body(ApiCommonResponse.success(response));
    }
}
