package com.pfplaybackend.api.user.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.user.domain.event.UserProfileChangedEvent;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.user.application.dto.command.UpdateBioCommand;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import com.pfplaybackend.api.user.adapter.out.persistence.MemberRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserBioCommandService {

    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void updateMyBio(UpdateBioCommand updateBioCommand) {
        AuthContext authContext = ThreadLocalContext.getAuthContext();
        MemberData memberData = memberRepository.findByUserId(authContext.getUserId()).orElseThrow();
        memberData.updateProfileBio(updateBioCommand.nickName(), updateBioCommand.introduction());
        memberRepository.save(memberData);
        eventPublisher.publishEvent(new UserProfileChangedEvent(memberData.getUserId()));
    }
}
