package com.pfplaybackend.api.user.application.service.temporary;

import com.pfplaybackend.api.config.oauth2.enums.ProviderType;
import com.pfplaybackend.api.playlist.application.service.PlaylistCommandService;
import com.pfplaybackend.api.playlist.application.service.PlaylistQueryService;
import com.pfplaybackend.api.user.application.dto.command.UpdateBioCommand;
import com.pfplaybackend.api.user.application.service.UserActivityService;
import com.pfplaybackend.api.user.application.service.UserProfileService;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Activity;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Guest;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Member;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Profile;
import com.pfplaybackend.api.user.domain.enums.ActivityType;
import com.pfplaybackend.api.user.domain.value.UserId;
import com.pfplaybackend.api.user.domain.value.WalletAddress;
import com.pfplaybackend.api.user.repository.GuestRepository;
import com.pfplaybackend.api.user.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TemporaryUserService {

    private final GuestRepository guestRepository;
    private final MemberRepository memberRepository;
    private final UserProfileService userProfileService;
    private final UserActivityService userActivityService;
    private final PlaylistCommandService playlistCommandService;

    @Transactional
    public void addTemporaryUsers() {
        UserId guestId = new UserId(UUID.fromString("d4e3f7a2-8df0-4f7b-a6b2-4d5b8e1c2f0e"));
        UserId associateMemberId = new UserId(UUID.fromString("3f7a2d4e-1c8b-4d2e-8a5f-7b4e8a6c2b1d"));
        UserId fullMemberId = new UserId(UUID.fromString("2a5b3c18-5a2e-45c6-9a5d-748b7a5e1b2c"));
        // Add Users
        addGuest(guestId);
        addAssociateMember(associateMemberId, "AM@google.com");
        // UpgradeToFullMember
        upgradeMember(addAssociateMember(fullMemberId, "FM@google.com"));
    }

    public void addGuest(UserId userId) {
        Guest guest = Guest.createWithFixedUserId(userId, "Firefox/MacOS");
        Profile profile = userProfileService.createProfileForGuest(guest);
        Guest updatedGuest = guest.initiateProfile(profile);
        guestRepository.save(updatedGuest.toData());
    }

    public Member addAssociateMember(UserId userId, String email) {
        Member member = Member.createWithFixedUserId(userId, email, ProviderType.GOOGLE);
        Profile profile = userProfileService.createProfileForMember(member);
        Map<ActivityType, Activity> activityMap = userActivityService.createUserActivities(member);
        Member updatedMember = member
                .initializeProfile(profile)
                .initializeActivityMap(activityMap);

        MemberData memberData = memberRepository.save(updatedMember.toData());
        playlistCommandService.createDefaultPlaylist(updatedMember.getUserId());
        return memberData.toDomain();
    }

    public Member upgradeMember(Member member) {
        // 1. Profile Update
        Member profileUpdatedMember = member.updateProfileBio(new UpdateBioCommand("nickname", "introduction"));
        memberRepository.save(profileUpdatedMember.toData());
        // 2. Wallet Update
        Member walletUpdatedMember = profileUpdatedMember.updateWalletAddress(new WalletAddress("wallet-address"));
        memberRepository.save(walletUpdatedMember.toData());
        return walletUpdatedMember;
    }
}
