package com.pfplaybackend.api.partyroom.repository;

import com.pfplaybackend.api.partyroom.model.entity.PartyroomPenalty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PartyroomPenaltyRepository extends JpaRepository<PartyroomPenalty, Long> {
    List<PartyroomPenalty> findPartyroomPaneltyByUserIdUidAndPartyroomId(UUID uid, String partyroomId);
}
