package com.pfplaybackend.api.partyroom.repository;

import com.pfplaybackend.api.partyroom.domain.model.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.domain.model.value.PartyroomId;
import com.pfplaybackend.api.user.domain.model.data.MemberData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

public interface PartyroomRepository extends JpaRepository<PartyroomData, PartyroomId> {
}
