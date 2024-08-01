package com.pfplaybackend.api.partyroom.repository.history;

import com.pfplaybackend.api.partyroom.domain.entity.data.history.UserGradeAdjustmentHistoryData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGradeAdjustmentHistoryRepository extends JpaRepository<UserGradeAdjustmentHistoryData, Long> {
}
