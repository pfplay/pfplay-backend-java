package com.pfplaybackend.api.user.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.user.domain.entity.data.GuestData;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import com.pfplaybackend.api.user.domain.entity.domainmodel.User;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.user.repository.GuestRepository;
import com.pfplaybackend.api.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserInfoService {

    private final GuestRepository guestRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public User getMyInfo () {
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
        if(authContext.getAuthorityTier() == AuthorityTier.GT) {
            GuestData guestData = guestRepository.findGuestByUserId(authContext.getUserId()).orElseThrow();
            return guestData.toDomain();
        }else {
            MemberData memberData = memberRepository.findByUserId(authContext.getUserId()).orElseThrow();
            return memberData.toDomain();
        }
    }
}
