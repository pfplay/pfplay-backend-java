package com.pfplaybackend.api.partyroom.repository;

import com.pfplaybackend.api.partyroom.domain.entity.data.deprecated.PartyroomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface PartyroomUserRepository extends JpaRepository<PartyroomUser, Long> {
    @Query("SELECT p.partyroomId FROM PartyroomUser p WHERE p.userId.uid = :uid")
    String findPartyroomIdByUserIdUid(UUID uid);
}
