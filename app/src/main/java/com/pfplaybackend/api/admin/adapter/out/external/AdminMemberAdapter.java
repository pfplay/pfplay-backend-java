package com.pfplaybackend.api.admin.adapter.out.external;

import com.pfplaybackend.api.admin.application.port.out.AdminMemberPort;
import com.pfplaybackend.api.common.config.security.enums.ProviderType;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.user.application.service.UserActivityCommandService;
import com.pfplaybackend.api.user.domain.entity.data.ActivityData;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import com.pfplaybackend.api.user.domain.enums.ActivityType;
import com.pfplaybackend.api.user.adapter.out.persistence.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminMemberAdapter implements AdminMemberPort {

    private final MemberRepository memberRepository;
    private final UserActivityCommandService userActivityCommandService;

    @Override
    public MemberData saveMember(MemberData member) {
        return memberRepository.save(member);
    }

    @Override
    public Optional<MemberData> findMemberById(Long id) {
        return memberRepository.findById(id);
    }

    @Override
    public void deleteMemberById(Long id) {
        memberRepository.deleteById(id);
    }

    @Override
    public Optional<MemberData> findMemberByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    @Override
    public long countMembersByProviderType(ProviderType providerType) {
        return memberRepository.countByProviderType(providerType);
    }

    @Override
    public Map<ActivityType, ActivityData> createUserActivities(UserId userId) {
        return userActivityCommandService.createUserActivities(userId);
    }
}
