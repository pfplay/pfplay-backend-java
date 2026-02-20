package com.pfplaybackend.api.party.adapter.out.persistence;

import com.pfplaybackend.api.party.domain.entity.data.DjData;
import com.pfplaybackend.api.party.domain.value.CrewId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DjRepository extends JpaRepository<DjData, Long> {
    List<DjData> findByPartyroomDataIdOrderByOrderNumberAsc(Long partyroomId);
    Optional<DjData> findByPartyroomDataIdAndCrewId(Long partyroomId, CrewId crewId);
    boolean existsByPartyroomDataId(Long partyroomId);
    boolean existsByPartyroomDataIdAndCrewId(Long partyroomId, CrewId crewId);
}
