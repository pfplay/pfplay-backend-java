package com.pfplaybackend.api.partyroom.repository;

import com.pfplaybackend.api.entity.PartyPermission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyPermissionRepository extends JpaRepository<PartyPermission, Integer> {
}
