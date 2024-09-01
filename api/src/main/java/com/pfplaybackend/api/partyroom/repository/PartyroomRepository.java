package com.pfplaybackend.api.partyroom.repository;

import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.repository.custom.PartyroomRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PartyroomRepository extends JpaRepository<PartyroomData, Long>, PartyroomRepositoryCustom {
    @Query("SELECT p FROM PartyroomData p LEFT JOIN FETCH p.partymemberDataList m WHERE p.id = :partyroomId AND m.isActive = true")
    Optional<PartyroomData> findByPartyroomId(@Param("partyroomId") Long partyroomId);

    Optional<PartyroomData> findByLinkDomain(String linkDomain);
}