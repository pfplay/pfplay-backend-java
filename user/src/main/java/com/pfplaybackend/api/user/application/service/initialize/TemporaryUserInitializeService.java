package com.pfplaybackend.api.user.application.service.initialize;

import com.pfplaybackend.api.common.config.security.enums.ProviderType;
import com.pfplaybackend.api.common.config.security.jwt.JwtService;
import com.pfplaybackend.api.user.application.port.out.PlaylistSetupPort;
import com.pfplaybackend.api.user.application.service.UserActivityService;
import com.pfplaybackend.api.user.application.service.UserProfileService;
import com.pfplaybackend.api.user.domain.entity.data.ProfileData;
import com.pfplaybackend.api.user.domain.entity.data.ActivityData;
import com.pfplaybackend.api.user.domain.entity.data.GuestData;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import com.pfplaybackend.api.user.domain.enums.ActivityType;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.user.domain.value.WalletAddress;
import com.pfplaybackend.api.user.adapter.out.persistence.GuestRepository;
import com.pfplaybackend.api.user.adapter.out.persistence.MemberRepository;
import org.springframework.transaction.annotation.Transactional;
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
    private final PlaylistSetupPort playlistSetupPort;
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
        // System.out.println("GT JWT: " + jwtService.generateNonExpiringAccessToken(TokenClaimsRequest.builder().uid(guest.getUserId().getUid().toString()).email("N/A").accessLevel(AccessLevel.ROLE_GUEST).authorityTier(AuthorityTier.GT).build()));
        guestRepository.save(guest);
    }

    public MemberData addAssociateMember(UserId userId, String email) {
        MemberData member = MemberData.createWithFixedUserId(userId, email, ProviderType.GOOGLE);
        ProfileData profile = userProfileService.createProfileDataForMember(member.getUserId());
        Map<ActivityType, ActivityData> activityMap = userActivityService.createUserActivities(member.getUserId());
        member.initializeProfile(profile);
        member.initializeActivityMap(activityMap);

        MemberData memberData = memberRepository.save(member);
        playlistSetupPort.createDefaultPlaylist(member.getUserId());
        // System.out.println("AM JWT: " + jwtService.generateNonExpiringAccessToken(TokenClaimsRequest.builder().uid(member.getUserId().getUid().toString()).email(member.getEmail()).accessLevel(AccessLevel.ROLE_MEMBER).authorityTier(member.getAuthorityTier()).build()));
        return memberData;
    }

    public MemberData upgradeMember(MemberData member) {
        // 1. Profile Update
        member.updateProfileBio("nickname", "introduction");
        memberRepository.save(member);
        // 2. Wallet Update
        member.updateWalletAddress(new WalletAddress("wallet-address"));
        memberRepository.save(member);
        // System.out.println("FM JWT: " + jwtService.generateNonExpiringAccessToken(TokenClaimsRequest.builder().uid(member.getUserId().getUid().toString()).email(member.getEmail()).accessLevel(AccessLevel.ROLE_MEMBER).authorityTier(member.getAuthorityTier()).build()));
        return member;
    }
}
