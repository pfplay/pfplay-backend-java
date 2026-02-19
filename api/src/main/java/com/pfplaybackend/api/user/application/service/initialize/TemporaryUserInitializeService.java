package com.pfplaybackend.api.user.application.service.initialize;

import com.pfplaybackend.api.common.config.security.enums.ProviderType;
import com.pfplaybackend.api.common.config.security.jwt.JwtService;
import com.pfplaybackend.api.playlist.application.service.PlaylistCommandService;
import com.pfplaybackend.api.user.application.dto.command.UpdateBioCommand;
import com.pfplaybackend.api.user.application.service.UserActivityService;
import com.pfplaybackend.api.profile.application.service.UserProfileService;
import com.pfplaybackend.api.profile.domain.ProfileData;
import com.pfplaybackend.api.user.domain.entity.data.ActivityData;
import com.pfplaybackend.api.user.domain.entity.data.GuestData;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import com.pfplaybackend.api.user.domain.enums.ActivityType;
import com.pfplaybackend.api.user.domain.value.UserId;
import com.pfplaybackend.api.user.domain.value.WalletAddress;
import com.pfplaybackend.api.user.adapter.out.persistence.GuestRepository;
import com.pfplaybackend.api.user.adapter.out.persistence.MemberRepository;
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
        GuestData guest = GuestData.createWithFixedUserId(userId, "Firefox/MacOS");
        ProfileData profile = userProfileService.createProfileDataForGuest(guest.getUserId());
        guest.initiateProfile(profile);
        // System.out.println("GT JWT: " + jwtService.generateNonExpiringAccessTokenForGuest(guest));
        guestRepository.save(guest);
    }

    public MemberData addAssociateMember(UserId userId, String email) {
        MemberData member = MemberData.createWithFixedUserId(userId, email, ProviderType.GOOGLE);
        ProfileData profile = userProfileService.createProfileDataForMember(member.getUserId());
        Map<ActivityType, ActivityData> activityMap = userActivityService.createUserActivities(member.getUserId());
        member.initializeProfile(profile);
        member.initializeActivityMap(activityMap);

        MemberData memberData = memberRepository.save(member);
        playlistCommandService.createDefaultPlaylist(member.getUserId());
        // System.out.println("AM JWT: " + jwtService.generateNonExpiringAccessTokenForMember(member));
        return memberData;
    }

    public MemberData upgradeMember(MemberData member) {
        // 1. Profile Update
        member.updateProfileBio(new UpdateBioCommand("nickname", "introduction"));
        memberRepository.save(member);
        // 2. Wallet Update
        member.updateWalletAddress(new WalletAddress("wallet-address"));
        memberRepository.save(member);
        // System.out.println("FM JWT: " + jwtService.generateNonExpiringAccessTokenForMember(member));
        return member;
    }
}
