package com.pfplaybackend.api.user.application.service;

import com.pfplaybackend.api.user.domain.entity.data.ActivityData;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import com.pfplaybackend.api.user.domain.enums.ActivityType;
import com.pfplaybackend.api.user.domain.value.UserId;
import com.pfplaybackend.api.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class UserActivityService {

    private final MemberRepository memberRepository;

    public Map<ActivityType, ActivityData> createUserActivities(UserId userId) {
        Map<ActivityType, ActivityData> activityDataMap = new HashMap<>();
        for (ActivityType activityType : ActivityType.values()) {
            activityDataMap.put(activityType, ActivityData.create(userId, activityType, 0));
        }
        return activityDataMap;
    }

    public void updateDjPointScore(UserId userId, int point) {
        MemberData memberData = memberRepository.findByUserId(userId).orElseThrow();
        memberData.updateDjScore(point);
        memberRepository.save(memberData);
    }

    public void updateRefererLinkScore(UserId userId) {

    }

    public void updatePartyroomActivationScore(UserId userId) {

    }
}
