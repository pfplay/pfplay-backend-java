package com.pfplaybackend.api.user.application;

import com.pfplaybackend.api.user.model.domain.MemberDomain;
import com.pfplaybackend.api.user.model.entity.Member;
import com.pfplaybackend.api.user.model.value.UserId;
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
    public MemberDomain findByUserId (UserId userId) {
        Member member = memberRepository.findCustomQueryMethod(userId).orElseThrow();
        MemberDomain memberDomain = member.toDomain();
        return memberDomain;
    }
}
