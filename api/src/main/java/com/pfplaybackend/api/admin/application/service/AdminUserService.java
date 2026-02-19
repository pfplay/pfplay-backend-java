package com.pfplaybackend.api.admin.application.service;

import com.pfplaybackend.api.common.config.security.enums.ProviderType;
import com.pfplaybackend.api.common.exception.http.ForbiddenException;
import com.pfplaybackend.api.common.exception.http.NotFoundException;
import com.pfplaybackend.api.playlist.application.service.PlaylistCommandService;
import com.pfplaybackend.api.user.application.service.UserActivityService;
import com.pfplaybackend.api.profile.domain.ProfileData;
import com.pfplaybackend.api.user.domain.entity.data.ActivityData;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import com.pfplaybackend.api.user.domain.enums.ActivityType;
import com.pfplaybackend.api.user.domain.value.AvatarBodyUri;
import com.pfplaybackend.api.user.domain.value.AvatarFaceUri;
import com.pfplaybackend.api.user.domain.value.UserId;
import com.pfplaybackend.api.user.domain.value.WalletAddress;
import com.pfplaybackend.api.user.adapter.out.persistence.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

/**
 * Service for managing virtual (admin-created) members
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final MemberRepository memberRepository;
    private final UserActivityService userActivityService;
    private final AdminProfileService adminProfileService;
    private final PlaylistCommandService playlistCommandService;

    /**
     * Create virtual member with auto-generated profile and FM authority
     *
     * @return Created virtual member with FM authority tier
     */
    @Transactional
    public MemberData createVirtualMember() {
        return createVirtualMember(null, null, null);
    }

    /**
     * Create virtual member with optional nickname and avatar customization
     * Final authority tier will be FM (Full Member)
     *
     * @param nickname Optional nickname (auto-generated if null)
     * @param avatarBodyUri Optional avatar body URI (default if null)
     * @param avatarFaceUri Optional avatar face URI (default if null)
     * @return Created virtual member with FM authority tier
     */
    @Transactional
    public MemberData createVirtualMember(
            String nickname,
            AvatarBodyUri avatarBodyUri,
            AvatarFaceUri avatarFaceUri) {

        // 1. Generate unique email for virtual member
        String virtualEmail = generateVirtualEmail();

        // 2. Create member with ADMIN provider type (initially AM)
        MemberData member = MemberData.create(virtualEmail, ProviderType.ADMIN);

        // 3. Create profile with auto-generated or provided values
        ProfileData profile = adminProfileService.createProfileForVirtualMember(
                member.getUserId(),
                nickname,
                avatarBodyUri,
                avatarFaceUri
        );

        // 4. Initialize activity map (all activities start at 0)
        Map<ActivityType, ActivityData> activityMap = userActivityService.createUserActivities(member.getUserId());

        // 5. Update member with profile and activities
        member.initializeProfile(profile);
        member.initializeActivityMap(activityMap);

        // 6. Save member (currently AM)
        MemberData savedMember = memberRepository.save(member);

        // 7. Create default GRABLIST playlist for virtual member
        playlistCommandService.createDefaultPlaylist(savedMember.getUserId());

        // 8. Upgrade to FM by setting wallet address
        // This automatically upgrades authority tier to FM
        savedMember.updateWalletAddress(new WalletAddress(""));

        // 9. Save upgraded member (now FM)
        MemberData finalData = memberRepository.save(savedMember);

        log.info("Virtual member created with GRABLIST: userId={}, email={}, nickname={}, authorityTier={}",
                finalData.getUserId().getUid(),
                virtualEmail,
                profile.getNickname(),
                finalData.getAuthorityTier());

        return finalData;
    }

    /**
     * Update avatar for existing virtual member
     *
     * @param userId User ID of virtual member
     * @param avatarBodyUri New avatar body URI (optional)
     * @param avatarFaceUri New avatar face URI (optional)
     * @return Updated member
     */
    @Transactional
    public MemberData updateVirtualMemberAvatar(
            UserId userId,
            AvatarBodyUri avatarBodyUri,
            AvatarFaceUri avatarFaceUri) {

        // 1. Find member
        MemberData member = findMemberByUserId(userId);

        // 2. Verify it's a virtual member (ADMIN provider type)
        if (member.getProviderType() != ProviderType.ADMIN) {
            throw new ForbiddenException("FORBIDDEN", "Cannot update avatar of non-virtual member");
        }

        // 3. Create new profile with updated avatar
        ProfileData updatedProfile = adminProfileService.createProfileForVirtualMember(
                userId,
                member.getProfileData().getNickname(),  // Keep existing nickname
                avatarBodyUri,
                avatarFaceUri
        );

        // 4. Update member with new profile
        member.initializeProfile(updatedProfile);

        // 5. Save and return
        MemberData savedData = memberRepository.save(member);

        log.info("Virtual member avatar updated: userId={}", userId.getUid());

        return savedData;
    }

    /**
     * Delete virtual member
     * Only ADMIN provider type members can be deleted
     *
     * @param userId User ID to delete
     */
    @Transactional
    public void deleteVirtualMember(UserId userId) {
        // 1. Find member
        MemberData member = findMemberByUserId(userId);

        // 2. Verify it's a virtual member
        if (member.getProviderType() != ProviderType.ADMIN) {
            throw new ForbiddenException("FORBIDDEN", "Cannot delete non-virtual member");
        }

        // 3. Delete
        memberRepository.deleteById(userId.getUid());

        log.info("Virtual member deleted: userId={}", userId.getUid());
    }

    /**
     * Get virtual member by user ID
     *
     * @param userId User ID
     * @return Virtual member
     */
    @Transactional(readOnly = true)
    public MemberData getVirtualMember(UserId userId) {
        MemberData member = findMemberByUserId(userId);

        if (member.getProviderType() != ProviderType.ADMIN) {
            throw new ForbiddenException("FORBIDDEN", "Not a virtual member");
        }

        return member;
    }

    /**
     * Find member by user ID
     *
     * @param userId User ID
     * @return MemberData entity
     */
    private MemberData findMemberByUserId(UserId userId) {
        return memberRepository.findById(userId.getUid())
                .orElseThrow(() -> new NotFoundException("NOT_FOUND", "Member not found: " + userId.getUid()));
    }

    /**
     * Generate unique email for virtual member
     * Pattern: virtual_{uuid}@pfplay.system
     *
     * @return Generated email address
     */
    private String generateVirtualEmail() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String email = "virtual_" + uuid + "@pfplay.system";

        // Check if email already exists (should be extremely rare)
        if (memberRepository.findByEmail(email).isPresent()) {
            // Recursive call to generate another email
            return generateVirtualEmail();
        }

        return email;
    }
}
