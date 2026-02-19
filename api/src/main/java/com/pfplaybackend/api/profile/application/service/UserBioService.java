package com.pfplaybackend.api.profile.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.profile.application.event.UserProfileEventService;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.user.application.dto.command.UpdateBioCommand;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
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
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
        MemberData memberData = memberRepository.findByUserId(authContext.getUserId()).orElseThrow();
        memberData.updateProfileBio(updateBioCommand);
        memberRepository.save(memberData);
        userProfileEventService.publishProfileChangedEvent(memberData);
    }
}
