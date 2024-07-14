package com.pfplaybackend.api.user.application.service.initialize;

import com.pfplaybackend.api.config.jwt.JwtProvider;
import com.pfplaybackend.api.config.oauth2.enums.ProviderType;
import com.pfplaybackend.api.user.application.dto.command.UpdateBioCommand;
import com.pfplaybackend.api.user.application.service.UserActivityService;
import com.pfplaybackend.api.user.application.service.UserProfileService;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Activity;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Member;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Profile;
import com.pfplaybackend.api.user.domain.enums.ActivityType;
import com.pfplaybackend.api.user.domain.value.UserId;
import com.pfplaybackend.api.user.domain.value.WalletAddress;
import com.pfplaybackend.api.user.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminUserInitializeService {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final UserProfileService userProfileService;
    private final UserActivityService userActivityService;

    @Transactional
    public void addAdminUser() {
        UserId adminId = new UserId(UUID.fromString("a4e3f7a2-87f0-4f7b-a6b2-4d5b8e1c2d0e"));
        upgradeMember(addAssociateMember(adminId));
    }

    private Member addAssociateMember(UserId userId) {
        Member member = Member.createWithFixedUserId(userId, "N/A", ProviderType.GOOGLE);
        Profile profile = userProfileService.createProfileForMember(member);
        Map<ActivityType, Activity> activityMap = userActivityService.createUserActivities(member);
        Member updatedMember = member
                .initializeProfile(profile)
                .initializeActivityMap(activityMap);
        MemberData memberData = memberRepository.save(updatedMember.toData());
        return memberData.toDomain();
    }

    private void upgradeMember(Member member) {
        // 1. Profile Update
        Member profileUpdatedMember = member.updateProfileBio(new UpdateBioCommand("운영자", ""));
        memberRepository.save(profileUpdatedMember.toData());
        // 2. Wallet Update
        Member walletUpdatedMember = profileUpdatedMember.updateWalletAddress(new WalletAddress(""));
        memberRepository.save(walletUpdatedMember.toData());
        // TODO jwt
    }
}
