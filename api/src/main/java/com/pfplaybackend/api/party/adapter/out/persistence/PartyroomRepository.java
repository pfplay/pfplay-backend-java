package com.pfplaybackend.api.party.adapter.out.persistence;

import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.value.LinkDomain;
import com.pfplaybackend.api.common.domain.value.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PartyroomRepository extends JpaRepository<PartyroomData, Long>, com.pfplaybackend.api.party.adapter.out.persistence.custom.PartyroomRepositoryCustom {
    Optional<PartyroomData> findByLinkDomain(LinkDomain linkDomain);
    @Query("SELECT p FROM PartyroomData p WHERE p.hostId = :userId AND p.isTerminated = false")
    Optional<PartyroomData> findActiveHostRoom(@Param("userId") UserId userId);
}