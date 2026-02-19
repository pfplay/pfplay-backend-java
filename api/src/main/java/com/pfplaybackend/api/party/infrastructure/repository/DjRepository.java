package com.pfplaybackend.api.party.infrastructure.repository;

import com.pfplaybackend.api.party.domain.entity.data.DjData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DjRepository extends JpaRepository<DjData, Long> {
}
