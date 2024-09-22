package com.pfplaybackend.api.partyroom.repository.history;

import com.pfplaybackend.api.partyroom.domain.entity.data.history.CrewPenaltyHistoryData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrewPenaltyHistoryRepository extends JpaRepository<CrewPenaltyHistoryData, Long> {
}
