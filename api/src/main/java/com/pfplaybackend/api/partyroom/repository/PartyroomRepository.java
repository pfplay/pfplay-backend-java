package com.pfplaybackend.api.partyroom.repository;

import com.pfplaybackend.api.partyroom.domain.model.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.domain.model.value.PartyroomId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyroomRepository extends JpaRepository<PartyroomData, PartyroomId> {
}
