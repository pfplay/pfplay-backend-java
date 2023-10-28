package com.pfplaybackend.api.pointhistory.repository;

import com.pfplaybackend.api.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
    @Query("SELECT type, SUM(point) AS point FROM PointHistory WHERE user.id = :userId GROUP BY type")
    List<List<Object>> getPointHistoryGroupBy(@Param("userId") Long userId);
}
