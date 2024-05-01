package com.pfplaybackend.api.user.application;

import com.pfplaybackend.api.user.repository.ActivityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class ActivityService {
    private ActivityRepository activityRepository;

//    public List<UserActivityGroupByDto> getPointHistory(Long userId) {
//        return userActivityRepository.getPointHistoriesByUserIdGroupByPointType(userId);
//    }
}