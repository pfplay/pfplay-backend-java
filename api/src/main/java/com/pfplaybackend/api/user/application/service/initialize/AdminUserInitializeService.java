package com.pfplaybackend.api.user.application.service.initialize;

import com.pfplaybackend.api.common.config.security.enums.ProviderType;
import com.pfplaybackend.api.user.application.dto.command.UpdateBioCommand;
import com.pfplaybackend.api.user.application.dto.shared.AvatarBodyDto;
import com.pfplaybackend.api.avatarresource.application.service.AvatarResourceService;
import com.pfplaybackend.api.user.application.service.UserActivityService;
import com.pfplaybackend.api.profile.application.service.UserProfileService;
import com.pfplaybackend.api.profile.domain.ProfileData;
import com.pfplaybackend.api.user.domain.entity.data.ActivityData;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import com.pfplaybackend.api.user.domain.enums.ActivityType;
import com.pfplaybackend.api.user.domain.service.UserAvatarDomainService;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.user.domain.value.*;
import com.pfplaybackend.api.user.adapter.out.persistence.MemberRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminUserInitializeService {

    private static final long ADMIN_FIXED_ID = 1000000000000000L;

    private final MemberRepository memberRepository;
    private final UserProfileService userProfileService;
    private final UserActivityService userActivityService;
    private final AvatarResourceService avatarResourceService;
    private final UserAvatarDomainService userAvatarDomainService;

    @Transactional
    public UserId addAdminUser() {
        UserId adminId = new UserId(ADMIN_FIXED_ID);
        MemberData member = addAssociateMember(adminId);
        MemberData updatedMember = updateAvatarBody(member, new AvatarBodyUri("https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_basic%2Fava_basic_003.png?alt=media"));
        upgradeMember(updatedMember);
        return adminId;
    }

    private MemberData addAssociateMember(UserId userId) {
        MemberData member = MemberData.createWithFixedUserId(userId, "N/A", ProviderType.GOOGLE);
        ProfileData profile = userProfileService.createProfileDataForMember(member.getUserId());
        Map<ActivityType, ActivityData> activityMap = userActivityService.createUserActivities(member.getUserId());
        member.initializeProfile(profile);
        member.initializeActivityMap(activityMap);
        return memberRepository.save(member);
    }

    private void upgradeMember(MemberData member) {
        // 1. Profile Update
        member.updateProfileBio(new UpdateBioCommand("운영자", ""));
        memberRepository.save(member);
        // 2. Wallet Update
        member.updateWalletAddress(new WalletAddress(""));
        memberRepository.save(member);
    }

    private MemberData updateAvatarBody(MemberData member, AvatarBodyUri avatarBodyUri) {
        AvatarBodyDto avatarBodyDto = avatarResourceService.findAvatarBodyByUri(avatarBodyUri);
        AvatarFaceUri avatarFaceUri = new AvatarFaceUri();
        AvatarIconUri avatarIconUri = userAvatarDomainService.findAvatarIconPairWithSingleBody(avatarBodyDto);

        // TODO Check if the score is actually configurable in Domain Service
        member.updateAvatarBody(avatarBodyDto);
        member.updateAvatarFace(avatarFaceUri);
        member.updateAvatarIcon(avatarIconUri);
        return memberRepository.save(member);
    }
}
