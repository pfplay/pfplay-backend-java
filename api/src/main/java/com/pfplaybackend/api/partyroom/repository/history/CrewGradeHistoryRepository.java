package com.pfplaybackend.api.partyroom.repository.history;

import com.pfplaybackend.api.partyroom.domain.entity.data.history.CrewGradeHistoryData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrewGradeHistoryRepository extends JpaRepository<CrewGradeHistoryData, Long> {
}
