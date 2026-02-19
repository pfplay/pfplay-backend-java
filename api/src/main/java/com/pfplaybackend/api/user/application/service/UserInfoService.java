package com.pfplaybackend.api.user.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.user.domain.entity.data.GuestData;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.user.adapter.in.web.payload.response.MyInfoResponse;
import com.pfplaybackend.api.user.adapter.out.persistence.GuestRepository;
import com.pfplaybackend.api.user.adapter.out.persistence.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserInfoService {

    private final GuestRepository guestRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public MyInfoResponse getMyInfo() {
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
        if (authContext.getAuthorityTier() == AuthorityTier.GT) {
            GuestData guest = guestRepository.findGuestByUserId(authContext.getUserId()).orElseThrow();
            return MyInfoResponse.fromGuest(guest);
        } else {
            MemberData member = memberRepository.findByUserId(authContext.getUserId()).orElseThrow();
            return MyInfoResponse.fromMember(member);
        }
    }
}
