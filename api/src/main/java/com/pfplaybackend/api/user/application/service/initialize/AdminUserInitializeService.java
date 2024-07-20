package com.pfplaybackend.api.user.application.service.initialize;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.config.jwt.JwtProvider;
import com.pfplaybackend.api.config.oauth2.enums.ProviderType;
import com.pfplaybackend.api.user.application.aspect.context.UserContext;
import com.pfplaybackend.api.user.application.dto.command.UpdateAvatarBodyCommand;
import com.pfplaybackend.api.user.application.dto.command.UpdateBioCommand;
import com.pfplaybackend.api.user.application.dto.shared.AvatarBodyDto;
import com.pfplaybackend.api.user.application.service.AvatarResourceService;
import com.pfplaybackend.api.user.application.service.UserActivityService;
import com.pfplaybackend.api.user.application.service.UserAvatarService;
import com.pfplaybackend.api.user.application.service.UserProfileService;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Activity;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Member;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Profile;
import com.pfplaybackend.api.user.domain.enums.ActivityType;
import com.pfplaybackend.api.user.domain.service.UserAvatarDomainService;
import com.pfplaybackend.api.user.domain.value.*;
import com.pfplaybackend.api.user.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminUserInitializeService {

    private final MemberRepository memberRepository;
    private final UserProfileService userProfileService;
    private final UserActivityService userActivityService;
    private final AvatarResourceService avatarResourceService;
    private final UserAvatarDomainService userAvatarDomainService;

    @Transactional
    public UserId addAdminUser() {
        UserId adminId = new UserId(UUID.fromString("a4e3f7a2-87f0-4f7b-a6b2-4d5b8e1c2d0e"));
        Member member = addAssociateMember(adminId);
        Member updatedMember = updateAvatarBody(member, new AvatarBodyUri("https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_basic%2Fava_basic_003.png?alt=media"));
        upgradeMember(updatedMember);
        return adminId;
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
    }

    private Member updateAvatarBody(Member member, AvatarBodyUri avatarBodyUri) {
        AvatarBodyDto avatarBodyDto = avatarResourceService.findAvatarBodyByUri(avatarBodyUri);
        AvatarFaceUri avatarFaceUri = userAvatarDomainService.updateFaceUriOnBodyUriChange(member, avatarBodyDto);
        AvatarIconUri avatarIconUri = userAvatarDomainService.updateIconUriOnBodyUriChange(member, avatarBodyDto);
        // TODO Check if the score is actually configurable in Domain Service
        Member updatedMember = member.updateAvatarBody(avatarBodyDto)
                .updateAvatarFace(avatarFaceUri)
                .updateAvatarIcon(avatarIconUri);
        MemberData memberData = memberRepository.save(updatedMember.toData());
        return memberData.toDomain();
    }
}
