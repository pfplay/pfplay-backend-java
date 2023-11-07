package com.pfplaybackend.api.pointhistory.repository;

import com.pfplaybackend.api.entity.PointHistory;
import com.pfplaybackend.api.pointhistory.presentation.dto.PointHistoryGroupByDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
    @Query(
            "SELECT " +
                    "new com.pfplaybackend.api.pointhistory.presentation.dto.PointHistoryGroupByDto(p.type, SUM(p.point))" +
                    "FROM PointHistory p " +
                    "WHERE p.user.id = :userId " +
                    "GROUP BY p.type"
    )
    List<PointHistoryGroupByDto> getPointHistoriesByUserIdGroupByPointType(@Param("userId") Long userId);
}
