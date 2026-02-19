package com.pfplaybackend.api.party.infrastructure.repository;

import com.pfplaybackend.api.party.domain.entity.data.DjData;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.user.domain.value.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DjRepository extends JpaRepository<DjData, Long> {
    List<DjData> findByPartyroomDataIdAndIsQueuedTrueOrderByOrderNumberAsc(Long partyroomId);
    Optional<DjData> findByPartyroomDataIdAndCrewId(Long partyroomId, CrewId crewId);
    Optional<DjData> findByPartyroomDataIdAndUserId(Long partyroomId, UserId userId);
    List<DjData> findByPartyroomDataId(Long partyroomId);
    boolean existsByPartyroomDataIdAndIsQueuedTrue(Long partyroomId);
    boolean existsByPartyroomDataIdAndUserIdAndIsQueuedTrue(Long partyroomId, UserId userId);
}
