package com.pfplaybackend.api.admin.presentation;

import com.pfplaybackend.api.admin.application.service.AdminUserService;
import com.pfplaybackend.api.admin.presentation.dto.request.VirtualMemberAvatarUpdateRequest;
import com.pfplaybackend.api.admin.presentation.dto.request.VirtualMemberCreateRequest;
import com.pfplaybackend.api.admin.presentation.dto.response.VirtualMemberResponse;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Member;
import com.pfplaybackend.api.user.domain.value.AvatarBodyUri;
import com.pfplaybackend.api.user.domain.value.AvatarFaceUri;
import com.pfplaybackend.api.user.domain.value.UserId;
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

    /**
     * Create virtual member
     *
     * POST /api/v1/admin/users/virtual
     *
     * Creates a new virtual member with:
     * - Auto-generated email (virtual_{uuid}@pfplay.system)
     * - FM authority tier
     * - Auto-generated or specified nickname
     * - Default or specified avatar
     *
     * @param request Virtual member creation request (all fields optional)
     * @return Created virtual member information
     */
    @PostMapping("/virtual")
    @PreAuthorize("hasAuthority('FM')")
    public ResponseEntity<VirtualMemberResponse> createVirtualMember(
            @Valid @RequestBody(required = false) VirtualMemberCreateRequest request) {

        // Handle empty body
        String nickname = request != null ? request.getNickname() : null;
        AvatarBodyUri avatarBodyUri = request != null && request.getAvatarBodyUri() != null
                ? new AvatarBodyUri(request.getAvatarBodyUri())
                : null;
        AvatarFaceUri avatarFaceUri = request != null && request.getAvatarFaceUri() != null
                ? new AvatarFaceUri(request.getAvatarFaceUri())
                : null;

        // Create virtual member
        Member member = adminUserService.createVirtualMember(nickname, avatarBodyUri, avatarFaceUri);

        // Build response
        VirtualMemberResponse response = buildResponse(member);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get virtual member information
     *
     * GET /api/v1/admin/users/virtual/{userId}
     *
     * @param userId User ID (UUID format)
     * @return Virtual member information
     */
    @GetMapping("/virtual/{userId}")
    @PreAuthorize("hasAuthority('FM')")
    public ResponseEntity<VirtualMemberResponse> getVirtualMember(@PathVariable String userId) {
        UserId userIdObj = new UserId(java.util.UUID.fromString(userId));
        Member member = adminUserService.getVirtualMember(userIdObj);

        VirtualMemberResponse response = buildResponse(member);

        return ResponseEntity.ok(response);
    }

    /**
     * Update virtual member avatar
     *
     * PUT /api/v1/admin/users/virtual/{userId}/avatar
     *
     * Updates avatar body and face URIs for an existing virtual member
     *
     * @param userId User ID (UUID format)
     * @param request Avatar update request
     * @return Updated virtual member information
     */
    @PutMapping("/virtual/{userId}/avatar")
    @PreAuthorize("hasAuthority('FM')")
    public ResponseEntity<VirtualMemberResponse> updateVirtualMemberAvatar(
            @PathVariable String userId,
            @Valid @RequestBody VirtualMemberAvatarUpdateRequest request) {

        UserId userIdObj = new UserId(java.util.UUID.fromString(userId));
        AvatarBodyUri avatarBodyUri = new AvatarBodyUri(request.getAvatarBodyUri());
        AvatarFaceUri avatarFaceUri = request.getAvatarFaceUri() != null
                ? new AvatarFaceUri(request.getAvatarFaceUri())
                : new AvatarFaceUri();

        Member updatedMember = adminUserService.updateVirtualMemberAvatar(
                userIdObj,
                avatarBodyUri,
                avatarFaceUri
        );

        VirtualMemberResponse response = buildResponse(updatedMember);

        return ResponseEntity.ok(response);
    }

    /**
     * Delete virtual member
     *
     * DELETE /api/v1/admin/users/virtual/{userId}
     *
     * Permanently deletes a virtual member
     * Only virtual members (ADMIN provider type) can be deleted
     *
     * @param userId User ID (UUID format)
     * @return No content
     */
    @DeleteMapping("/virtual/{userId}")
    @PreAuthorize("hasAuthority('FM')")
    public ResponseEntity<Void> deleteVirtualMember(@PathVariable String userId) {
        UserId userIdObj = new UserId(java.util.UUID.fromString(userId));
        adminUserService.deleteVirtualMember(userIdObj);

        return ResponseEntity.noContent().build();
    }

    /**
     * Build response DTO from Member domain object
     *
     * @param member Member domain object
     * @return VirtualMemberResponse
     */
    private VirtualMemberResponse buildResponse(Member member) {
        return VirtualMemberResponse.builder()
                .userId(member.getUserId().getUid().toString())
                .email(member.getEmail())
                .nickname(member.getProfile().getNickname())
                .introduction(member.getProfile().getIntroduction())
                .providerType(member.getProviderType())
                .authorityTier(member.getAuthorityTier())
                .avatarBodyUri(member.getProfile().getAvatarBodyUri().getAvatarBodyUri())
                .avatarFaceUri(member.getProfile().getAvatarFaceUri().getAvatarFaceUri())
                .avatarIconUri(member.getProfile().getAvatarIconUri().getAvatarIconUri())
                .avatarCompositionType(member.getProfile().getAvatarCompositionType())
                .combinePositionX(member.getProfile().getCombinePositionX())
                .combinePositionY(member.getProfile().getCombinePositionY())
                .offsetX(member.getProfile().getOffsetX())
                .offsetY(member.getProfile().getOffsetY())
                .scale(member.getProfile().getScale())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}
