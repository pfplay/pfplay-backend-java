package com.pfplaybackend.api.user.repository.activity;

import com.pfplaybackend.api.user.model.entity.activity.UserActivity;
import com.pfplaybackend.api.user.presentation.activity.dto.UserActivityGroupByDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {
    @Query(
            "SELECT " +
                    "UserActivityGroupByDto(p.type, SUM(p.point))" +
                    "FROM UserActivity p " +
                    "WHERE p.user.id = :userId " +
                    "GROUP BY p.type"
    )
    List<UserActivityGroupByDto> getPointHistoriesByUserIdGroupByPointType(@Param("userId") Long userId);
}
