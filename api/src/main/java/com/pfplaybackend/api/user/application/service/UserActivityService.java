package com.pfplaybackend.api.user.application.service;

import com.pfplaybackend.api.user.domain.entity.domainmodel.Activity;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Member;
import com.pfplaybackend.api.user.domain.enums.ActivityType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


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
}