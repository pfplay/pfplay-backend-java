package com.pfplaybackend.api.user.repository;

import com.pfplaybackend.api.user.model.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
//    @Query(
//            "SELECT " +
//                    "UserActivityGroupByDto(p.type, SUM(p.point))" +
//                    "FROM UserActivity p " +
//                    "WHERE p.user.id = :userId " +
//                    "GROUP BY p.type"
//    )
//    List<UserActivityGroupByDto> getPointHistoriesByUserIdGroupByPointType(@Param("userId") Long userId);
}
