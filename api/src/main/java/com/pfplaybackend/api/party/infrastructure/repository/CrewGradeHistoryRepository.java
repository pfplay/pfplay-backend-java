package com.pfplaybackend.api.party.infrastructure.repository;

import com.pfplaybackend.api.party.domain.entity.data.history.CrewGradeHistoryData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrewGradeHistoryRepository extends JpaRepository<CrewGradeHistoryData, Long> {
}
