package com.pfplaybackend.api.partyroom.repository;

import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomSessionData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<PartyroomSessionData, Long> {
}
