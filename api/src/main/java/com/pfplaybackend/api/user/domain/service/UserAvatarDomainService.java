package com.pfplaybackend.api.user.domain.service;

import com.pfplaybackend.api.user.application.dto.shared.AvatarBodyDto;
import com.pfplaybackend.api.user.domain.model.domain.Activity;
import com.pfplaybackend.api.user.domain.model.enums.ActivityType;
import com.pfplaybackend.api.user.domain.model.enums.ObtainmentType;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserAvatarDomainService {
    public boolean isAvailableBody(AvatarBodyDto avatarBodyDto, Map<ActivityType, Activity> activityMap) {
        ObtainmentType obtainmentType = avatarBodyDto.getObtainableType();
        if(obtainmentType.equals(ObtainmentType.BASIC)) return true;
        // Comparison of Scores
        ActivityType activityType = convertToActivityType(avatarBodyDto.getObtainableType());
        return activityMap.get(activityType).getScore() >= avatarBodyDto.getObtainableScore();
    }

    private ActivityType convertToActivityType(ObtainmentType obtainmentType) {
        switch (obtainmentType) {
            case DJ_PNT -> {
                return ActivityType.DJ_PNT;
            }
            case REF_LINK -> {
                return ActivityType.REF_LINK;
            }
            case ROOM_ACT -> {
                return ActivityType.ROOM_ACT;
            }
        }
        return null;
    }
}
