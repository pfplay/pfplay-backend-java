package com.pfplaybackend.api.admin.adapter.in.web;

import com.pfplaybackend.api.admin.adapter.in.web.payload.request.CreateVirtualMemberRequest;
import com.pfplaybackend.api.admin.adapter.in.web.payload.request.UpdateVirtualMemberAvatarRequest;
import com.pfplaybackend.api.admin.adapter.in.web.payload.response.QueryVirtualMemberResponse;
import com.pfplaybackend.api.admin.application.service.AdminUserService;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import com.pfplaybackend.api.user.domain.value.AvatarBodyUri;
import com.pfplaybackend.api.user.domain.value.AvatarFaceUri;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin API Controller for managing virtual members
 * Only accessible by Full Members (FM authority tier)
 */
@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @PostMapping("/virtual")
    @PreAuthorize("hasAuthority('FM')")
    public ResponseEntity<QueryVirtualMemberResponse> createVirtualMember(
            @Valid @RequestBody(required = false) CreateVirtualMemberRequest request) {

        String nickname = request != null ? request.getNickname() : null;
        AvatarBodyUri avatarBodyUri = request != null && request.getAvatarBodyUri() != null
                ? new AvatarBodyUri(request.getAvatarBodyUri())
                : null;
        AvatarFaceUri avatarFaceUri = request != null && request.getAvatarFaceUri() != null
                ? new AvatarFaceUri(request.getAvatarFaceUri())
                : null;

        MemberData member = adminUserService.createVirtualMember(nickname, avatarBodyUri, avatarFaceUri);
        QueryVirtualMemberResponse response = buildResponse(member);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/virtual/{userId}")
    @PreAuthorize("hasAuthority('FM')")
    public ResponseEntity<QueryVirtualMemberResponse> getVirtualMember(@PathVariable String userId) {
        UserId userIdObj = UserId.fromString(userId);
        MemberData member = adminUserService.getVirtualMember(userIdObj);
        QueryVirtualMemberResponse response = buildResponse(member);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/virtual/{userId}/avatar")
    @PreAuthorize("hasAuthority('FM')")
    public ResponseEntity<QueryVirtualMemberResponse> updateVirtualMemberAvatar(
            @PathVariable String userId,
            @Valid @RequestBody UpdateVirtualMemberAvatarRequest request) {

        UserId userIdObj = UserId.fromString(userId);
        AvatarBodyUri avatarBodyUri = new AvatarBodyUri(request.getAvatarBodyUri());
        AvatarFaceUri avatarFaceUri = request.getAvatarFaceUri() != null
                ? new AvatarFaceUri(request.getAvatarFaceUri())
                : new AvatarFaceUri();

        MemberData updatedMember = adminUserService.updateVirtualMemberAvatar(
                userIdObj,
                avatarBodyUri,
                avatarFaceUri
        );

        QueryVirtualMemberResponse response = buildResponse(updatedMember);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/virtual/{userId}")
    @PreAuthorize("hasAuthority('FM')")
    public ResponseEntity<Void> deleteVirtualMember(@PathVariable String userId) {
        UserId userIdObj = UserId.fromString(userId);
        adminUserService.deleteVirtualMember(userIdObj);

        return ResponseEntity.noContent().build();
    }

    private QueryVirtualMemberResponse buildResponse(MemberData member) {
        var avatar = member.getProfileData().getAvatarSetting();
        return QueryVirtualMemberResponse.builder()
                .userId(member.getUserId().getUid().toString())
                .email(member.getEmail())
                .nickname(member.getProfileData().getNicknameValue())
                .introduction(member.getProfileData().getIntroduction())
                .providerType(member.getProviderType())
                .authorityTier(member.getAuthorityTier())
                .avatarBodyUri(avatar.getAvatarBodyUri().getAvatarBodyUri())
                .avatarFaceUri(avatar.getAvatarFaceUri().getAvatarFaceUri())
                .avatarIconUri(avatar.getAvatarIconUri().getAvatarIconUri())
                .avatarCompositionType(avatar.getAvatarCompositionType())
                .combinePositionX(avatar.getCombinePositionX())
                .combinePositionY(avatar.getCombinePositionY())
                .offsetX(avatar.getOffsetX())
                .offsetY(avatar.getOffsetY())
                .scale(avatar.getScale())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}
