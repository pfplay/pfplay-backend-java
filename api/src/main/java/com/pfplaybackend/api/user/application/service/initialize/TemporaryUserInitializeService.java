package com.pfplaybackend.api.user.application.service.initialize;

import com.pfplaybackend.api.common.config.security.enums.ProviderType;
import com.pfplaybackend.api.common.config.security.jwt.JwtService;
import com.pfplaybackend.api.playlist.application.service.PlaylistCommandService;
import com.pfplaybackend.api.user.application.dto.command.UpdateBioCommand;
import com.pfplaybackend.api.user.application.service.UserActivityService;
import com.pfplaybackend.api.profile.application.service.UserProfileService;
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

@Service
@RequiredArgsConstructor
public class TemporaryUserInitializeService {

    private final GuestRepository guestRepository;
    private final MemberRepository memberRepository;
    private final UserProfileService userProfileService;
    private final UserActivityService userActivityService;
    private final PlaylistCommandService playlistCommandService;
    private final JwtService jwtService;

    private static final long GUEST_FIXED_ID = 1000000000000001L;
    private static final long ASSOCIATE_MEMBER_FIXED_ID = 1000000000000002L;
    private static final long FULL_MEMBER_FIXED_ID = 1000000000000003L;

    @Transactional
    public void addTemporaryUsers() {
        UserId guestId = new UserId(GUEST_FIXED_ID);
        UserId associateMemberId = new UserId(ASSOCIATE_MEMBER_FIXED_ID);
        UserId fullMemberId = new UserId(FULL_MEMBER_FIXED_ID);
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
        // System.out.println("GT JWT: " + jwtService.generateNonExpiringAccessTokenForGuest(guest));
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
        // System.out.println("AM JWT: " + jwtService.generateNonExpiringAccessTokenForMember(updatedMember));
        return memberData.toDomain();
    }

    public Member upgradeMember(Member member) {
        // 1. Profile Update
        Member profileUpdatedMember = member.updateProfileBio(new UpdateBioCommand("nickname", "introduction"));
        memberRepository.save(profileUpdatedMember.toData());
        // 2. Wallet Update
        Member walletUpdatedMember = profileUpdatedMember.updateWalletAddress(new WalletAddress("wallet-address"));
        memberRepository.save(walletUpdatedMember.toData());
        // System.out.println("FM JWT: " + jwtService.generateNonExpiringAccessTokenForMember(walletUpdatedMember));
        return walletUpdatedMember;
    }
}
