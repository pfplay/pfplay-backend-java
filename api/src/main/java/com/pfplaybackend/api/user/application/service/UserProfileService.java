package com.pfplaybackend.api.user.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.config.jwt.dto.UserCredentials;
import com.pfplaybackend.api.user.application.dto.command.UpdateBioCommand;
import com.pfplaybackend.api.user.application.aspect.context.UserContext;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSummaryDto;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import com.pfplaybackend.api.user.domain.entity.domainmodel.AvatarResource;
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
                .withAvatarBodyUri(userAvatarService.getDefaultAvatarBodyUri())
                .withAvatarFaceUri(userAvatarService.getDefaultAvatarFaceUri());
    }

    public Profile createProfileForMember(Member member) {
        return new Profile(member.getUserId());
    }

    @Transactional
    public void updateMyBio(UpdateBioCommand updateBioCommand) {
        UserContext userContext = (UserContext) ThreadLocalContext.getContext();
        MemberData memberData = memberRepository.findByUserId(userContext.getUserId()).orElseThrow();
        Member member = memberData.toDomain();
        Member updatedMember = member.updateProfileBio(updateBioCommand);
        memberRepository.save(updatedMember.toData());
    }

    public ProfileSummaryDto getMyProfileSummary() {
        UserContext userContext = (UserContext) ThreadLocalContext.getContext();
        if(userDomainService.isGuest(userContext)) {
            GuestData guestData = guestRepository.findByUserId(userContext.getUserId()).orElseThrow();
            return guestData.toDomain().getProfileSummary();
        }else {
            MemberData memberData = memberRepository.findByUserId(userContext.getUserId()).orElseThrow();
            return memberData.toDomain().getProfileSummary();
        }
    }

    public ProfileSummaryDto getOtherProfileSummary(UserId otherUserId, AuthorityTier authorityTier) {
        // TODO 타인의 프로필을 조회할 수 있는 공간은 파티룸 내에서만 가능하다.
        // TODO 즉, 타인의 프로필을 조회할 때 대상의 'Guest 여부'를 지정해서 보내줘야 한다.
        // TODO 위 조건이 가능하려면 파티룸 내의 모든 사람을 조회할 때 게스트 여부를 지정해서 리턴해주면 된다.
        if(userDomainService.isGuest(authorityTier)) {
            GuestData guestData = guestRepository.findByUserId(otherUserId).orElseThrow();
            return guestData.toDomain().getProfileSummary();
        }else {
            MemberData memberData = memberRepository.findByUserId(otherUserId).orElseThrow();
            return memberData.toDomain().getProfileSummary();
        }
    }
}
