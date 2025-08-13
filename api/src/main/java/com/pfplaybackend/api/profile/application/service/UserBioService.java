package com.pfplaybackend.api.profile.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.profile.application.event.UserProfileEventService;
import com.pfplaybackend.api.profile.domain.repository.UserProfileRepository;
import com.pfplaybackend.api.user.application.aspect.context.UserContext;
import com.pfplaybackend.api.user.application.dto.command.UpdateBioCommand;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Member;
import com.pfplaybackend.api.user.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserBioService {

    private final MemberRepository memberRepository;
    private final UserProfileEventService userProfileEventService;

    @Transactional
    public void updateMyBio(UpdateBioCommand updateBioCommand) {
        UserContext userContext = (UserContext) ThreadLocalContext.getContext();
        MemberData memberData = memberRepository.findByUserId(userContext.getUserId()).orElseThrow();
        Member member = memberData.toDomain();
        Member updatedMember = member.updateProfileBio(updateBioCommand);
        memberRepository.save(updatedMember.toData());
        userProfileEventService.publishProfileChangedEvent(updatedMember);
    }
}
