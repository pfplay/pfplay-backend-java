package com.pfplaybackend.api.party.infrastructure.repository;

import com.pfplaybackend.api.party.domain.entity.data.history.CrewBlockHistoryData;
import com.pfplaybackend.api.party.domain.value.CrewId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CrewBlockHistoryRepository extends JpaRepository<CrewBlockHistoryData, Long> {
    List<CrewBlockHistoryData> findAllByBlockerCrewIdAndUnblockedIsFalse(CrewId crewId);
    Optional<CrewBlockHistoryData> findByIdAndBlockerCrewIdAndUnblockedIsFalse(Long id, CrewId blockerCrewId);
    Optional<CrewBlockHistoryData> findByBlockerCrewIdAndBlockedCrewIdAndUnblockedIsFalse(CrewId blockerCrewId, CrewId blockedCrewId);
}
