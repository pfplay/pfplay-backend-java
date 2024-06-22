package com.pfplaybackend.api.user.application.service;

import com.pfplaybackend.api.user.domain.model.domain.Activity;
import com.pfplaybackend.api.user.domain.model.domain.Member;
import com.pfplaybackend.api.user.domain.model.enums.ActivityType;
import com.pfplaybackend.api.user.domain.service.UserDomainService;
import com.pfplaybackend.api.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserActivityService {

    public Map<ActivityType, Activity> createUserActivities(Member member) {
        Map<ActivityType, Activity> activityDomains = new HashMap<>();
        for (ActivityType activityType : ActivityType.values()) {
            activityDomains.put(activityType, new Activity(member.getUserId(), activityType, 0));
        }
        return activityDomains;
    }

    public void updateDJPointScore(UUID uuid, int point) {
        // TODO 취소가 가능하기 때문에 마이너스가 될 수도 있다.
    }

    public void updateRefererLinkScore(UUID uuid) {

    }

    public void updatePartyroomActivationScore(UUID uuid) {

    }
}