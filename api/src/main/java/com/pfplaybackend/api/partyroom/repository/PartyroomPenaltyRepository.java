package com.pfplaybackend.api.partyroom.repository;

import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomPenaltyHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PartyroomPenaltyRepository extends JpaRepository<PartyroomPenaltyHistory, Long> {
    List<PartyroomPenaltyHistory> findPartyroomPenaltyHistoriesByUserIdUidAndPartyroomId(UUID uid, String partyroomId);
}
