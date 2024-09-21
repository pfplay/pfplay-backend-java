package com.pfplaybackend.api.partyroom.repository;

import com.pfplaybackend.api.partyroom.domain.entity.data.CrewData;
import com.pfplaybackend.api.user.domain.value.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CrewRepository extends JpaRepository<CrewData, Long> {

    @Query("SELECT p FROM CrewData p WHERE p.userId = :userId AND p.isActive = true")
    Optional<CrewData> findByUserId(@Param("userId") UserId userId);
}
