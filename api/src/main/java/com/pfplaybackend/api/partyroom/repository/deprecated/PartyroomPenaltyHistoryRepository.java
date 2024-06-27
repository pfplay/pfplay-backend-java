package com.pfplaybackend.api.partyroom.repository.deprecated;

import com.pfplaybackend.api.partyroom.domain.entity.data.deprecated.PartyroomPenaltyHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PartyroomPenaltyHistoryRepository extends JpaRepository<PartyroomPenaltyHistory, Long> {
    List<PartyroomPenaltyHistory> findPartyroomPenaltyHistoriesByUserIdUidAndPartyroomId(UUID uid, String partyroomId);
}