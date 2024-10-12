package com.pfplaybackend.api.partyroom.repository;

import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.repository.custom.PartyroomRepositoryCustom;
import com.pfplaybackend.api.user.domain.value.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PartyroomRepository extends JpaRepository<PartyroomData, Long>, PartyroomRepositoryCustom {
    Optional<PartyroomData> findByLinkDomain(String linkDomain);
    @Query("SELECT p FROM PartyroomData p WHERE p.hostId = :userId AND p.isTerminated = false")
    Optional<PartyroomData> findActiveHostRoom(@Param("userId") UserId userId);
}