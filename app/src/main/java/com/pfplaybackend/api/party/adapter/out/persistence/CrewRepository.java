package com.pfplaybackend.api.party.adapter.out.persistence;

import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CrewRepository extends JpaRepository<CrewData, Long> {
    List<CrewData> findByPartyroomIdAndIsActiveTrue(PartyroomId partyroomId);
    Optional<CrewData> findByPartyroomIdAndUserId(PartyroomId partyroomId, UserId userId);
    long countByPartyroomIdAndIsActiveTrue(PartyroomId partyroomId);
}
