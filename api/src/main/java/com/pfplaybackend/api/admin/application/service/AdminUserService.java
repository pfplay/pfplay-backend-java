package com.pfplaybackend.api.admin.application.service;

import com.pfplaybackend.api.common.config.security.enums.ProviderType;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.exception.http.ForbiddenException;
import com.pfplaybackend.api.common.exception.http.NotFoundException;
import com.pfplaybackend.api.playlist.application.service.PlaylistCommandService;
import com.pfplaybackend.api.user.application.service.UserActivityService;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Activity;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Member;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Profile;
import com.pfplaybackend.api.user.domain.enums.ActivityType;
import com.pfplaybackend.api.user.domain.value.AvatarBodyUri;
import com.pfplaybackend.api.user.domain.value.AvatarFaceUri;
import com.pfplaybackend.api.user.domain.value.UserId;
import com.pfplaybackend.api.user.domain.value.WalletAddress;
import com.pfplaybackend.api.user.repository.MemberRepository;
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
    public Member createVirtualMember() {
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
    public Member createVirtualMember(
            String nickname,
            AvatarBodyUri avatarBodyUri,
            AvatarFaceUri avatarFaceUri) {

        // 1. Generate unique email for virtual member
        String virtualEmail = generateVirtualEmail();

        // 2. Create member with ADMIN provider type (initially AM)
        Member member = Member.create(virtualEmail, ProviderType.ADMIN);

        // 3. Create profile with auto-generated or provided values
        Profile profile = adminProfileService.createProfileForVirtualMember(
                member.getUserId(),
                nickname,
                avatarBodyUri,
                avatarFaceUri
        );

        // 4. Initialize activity map (all activities start at 0)
        Map<ActivityType, Activity> activityMap = userActivityService.createUserActivities(member);

        // 5. Update member with profile and activities
        Member memberWithProfile = member
                .initializeProfile(profile)
                .initializeActivityMap(activityMap);

        // 6. Save member (currently AM)
        MemberData savedData = memberRepository.save(memberWithProfile.toData());
        Member savedMember = savedData.toDomain();

        // 7. Create default GRABLIST playlist for virtual member
        playlistCommandService.createDefaultPlaylist(savedMember.getUserId());

        // 8. Upgrade to FM by setting wallet address
        // This automatically upgrades authority tier to FM
        Member upgradedMember = savedMember.updateWalletAddress(new WalletAddress(""));

        // 9. Save upgraded member (now FM)
        MemberData finalData = memberRepository.save(upgradedMember.toData());

        log.info("Virtual member created with GRABLIST: userId={}, email={}, nickname={}, authorityTier={}",
                finalData.getUserId().getUid(),
                virtualEmail,
                profile.getNickname(),
                AuthorityTier.FM);

        return finalData.toDomain();
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
    public Member updateVirtualMemberAvatar(
            UserId userId,
            AvatarBodyUri avatarBodyUri,
            AvatarFaceUri avatarFaceUri) {

        // 1. Find member
        Member member = findMemberByUserId(userId);

        // 2. Verify it's a virtual member (ADMIN provider type)
        if (member.getProviderType() != ProviderType.ADMIN) {
            throw new ForbiddenException("FORBIDDEN", "Cannot update avatar of non-virtual member");
        }

        // 3. Create new profile with updated avatar
        Profile updatedProfile = adminProfileService.createProfileForVirtualMember(
                userId,
                member.getProfile().getNickname(),  // Keep existing nickname
                avatarBodyUri,
                avatarFaceUri
        );

        // 4. Update member with new profile
        Member updatedMember = member.toBuilder()
                .profile(updatedProfile)
                .build();

        // 5. Save and return
        MemberData savedData = memberRepository.save(updatedMember.toData());

        log.info("Virtual member avatar updated: userId={}", userId.getUid());

        return savedData.toDomain();
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
        Member member = findMemberByUserId(userId);

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
    public Member getVirtualMember(UserId userId) {
        Member member = findMemberByUserId(userId);

        if (member.getProviderType() != ProviderType.ADMIN) {
            throw new ForbiddenException("FORBIDDEN", "Not a virtual member");
        }

        return member;
    }

    /**
     * Find member by user ID
     *
     * @param userId User ID
     * @return Member domain object
     */
    private Member findMemberByUserId(UserId userId) {
        return memberRepository.findById(userId.getUid())
                .map(MemberData::toDomain)
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
