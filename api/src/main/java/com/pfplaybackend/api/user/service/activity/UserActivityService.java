package com.pfplaybackend.api.user.service.activity;

import com.pfplaybackend.api.user.presentation.activity.dto.UserActivityGroupByDto;
import com.pfplaybackend.api.user.repository.activity.UserActivityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
public class UserActivityService {
    private UserActivityRepository userActivityRepository;

    public List<UserActivityGroupByDto> getPointHistory(Long userId) {
        return userActivityRepository.getPointHistoriesByUserIdGroupByPointType(userId);
    }
}