package com.pfplaybackend.api.partyroom.repository;

import com.pfplaybackend.api.partyroom.domain.entity.data.PartymemberData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PartymemberRepository extends JpaRepository<PartymemberData, Long> {
    Optional<PartymemberData> findByUserIdUid(UUID uid);
}
