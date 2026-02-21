package com.pfplaybackend.api.party.adapter.out.persistence;

import com.pfplaybackend.api.party.domain.entity.data.DjData;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DjRepository extends JpaRepository<DjData, Long> {
    List<DjData> findByPartyroomIdOrderByOrderNumberAsc(PartyroomId partyroomId);
    Optional<DjData> findByPartyroomIdAndCrewId(PartyroomId partyroomId, CrewId crewId);
    boolean existsByPartyroomId(PartyroomId partyroomId);
    boolean existsByPartyroomIdAndCrewId(PartyroomId partyroomId, CrewId crewId);
}
