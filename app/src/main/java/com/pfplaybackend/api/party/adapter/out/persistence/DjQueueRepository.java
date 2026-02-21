package com.pfplaybackend.api.party.adapter.out.persistence;

import com.pfplaybackend.api.party.domain.entity.data.DjQueueData;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DjQueueRepository extends JpaRepository<DjQueueData, PartyroomId> {
}
