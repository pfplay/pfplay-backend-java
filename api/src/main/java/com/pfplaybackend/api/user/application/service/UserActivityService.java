package com.pfplaybackend.api.user.application.service;

import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Activity;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Member;
import com.pfplaybackend.api.user.domain.enums.ActivityType;
import com.pfplaybackend.api.user.domain.value.UserId;
import com.pfplaybackend.api.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserActivityService {

    private final MemberRepository memberRepository;

    public Map<ActivityType, Activity> createUserActivities(Member member) {
        Map<ActivityType, Activity> activityDomains = new HashMap<>();
        for (ActivityType activityType : ActivityType.values()) {
            activityDomains.put(activityType, new Activity(member.getUserId(), activityType, 0));
        }
        return activityDomains;
    }

    public void updateDjPointScore(UserId userId, int point) {
        MemberData memberData = memberRepository.findByUserId(userId).orElseThrow();
        Member member = memberData.toDomain();
        Member updatedMember = member.updateDjScore(point);
        memberRepository.save(updatedMember.toData());
    }

    public void updateRefererLinkScore(UserId userId) {

    }

    public void updatePartyroomActivationScore(UserId userId) {

    }
}