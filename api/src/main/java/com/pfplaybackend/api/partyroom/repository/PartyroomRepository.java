package com.pfplaybackend.api.partyroom.repository;

import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
public interface PartyroomRepository extends JpaRepository<PartyroomData, PartyroomId> {
    PartyroomUser findByUserId(String userId);
    String findChatroomIdByUserIdUid(UUID uid);
}
