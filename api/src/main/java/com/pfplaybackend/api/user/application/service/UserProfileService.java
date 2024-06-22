package com.pfplaybackend.api.user.application.service;

import com.pfplaybackend.api.config.jwt.dto.UserCredentials;
import com.pfplaybackend.api.user.application.dto.command.UpdateBioCommand;
import com.pfplaybackend.api.user.application.aspect.context.UserContext;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSummaryDto;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Guest;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Member;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Profile;
import com.pfplaybackend.api.user.domain.service.GuestDomainService;
import com.pfplaybackend.api.user.domain.entity.data.GuestData;
import com.pfplaybackend.api.user.domain.value.UserId;
import com.pfplaybackend.api.user.domain.service.UserDomainService;
import com.pfplaybackend.api.user.repository.GuestRepository;
import com.pfplaybackend.api.user.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    final private GuestRepository guestRepository;
    final private MemberRepository memberRepository;
    private final UserDomainService userDomainService;
    final private GuestDomainService guestDomainService;
    final private UserAvatarService userAvatarService;

    public Profile createProfileForGuest(Guest guest) {
        Profile profile = new Profile(guest.getUserId());
        return profile
                .withNickname(guestDomainService.generateRandomNickname())
                .withAvatarBodyUri(userAvatarService.getDefaultAvatarBodyUri());
    }

    public Profile createProfileForMember(Member member) {
        return new Profile(member.getUserId());
    }

    public ProfileSummaryDto getMyProfileSummary() {
        UserCredentials userCredentials = UserContext.getUserCredentials();
        if(userDomainService.isGuest(userCredentials)) {
            GuestData guestData = guestRepository.findByUserId(userCredentials.getUserId()).orElseThrow();
            return guestData.toDomain().getProfileSummary();
        }else {
            MemberData memberData = memberRepository.findByUserId(userCredentials.getUserId()).orElseThrow();
            return memberData.toDomain().getProfileSummary();
        }
    }

    @Transactional
    public void updateMyBio(UpdateBioCommand updateBioCommand) {
        UserCredentials userCredentials = UserContext.getUserCredentials();
        UserId userId = userCredentials.getUserId();
        MemberData memberData = memberRepository.findByUserId(userId).orElseThrow();
        Member member = memberData.toDomain();
        Member updatedMember = member.updateProfileBio(updateBioCommand);
        memberRepository.save(updatedMember.toData());
    }

    public Profile getOtherProfile(UserId userId) {
        return null;
    }
}
