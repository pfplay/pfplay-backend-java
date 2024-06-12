package com.pfplaybackend.api.partyroom.repository;

import com.pfplaybackend.api.partyroom.domain.model.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.domain.model.value.PartyroomId;
import com.pfplaybackend.api.user.domain.model.data.MemberData;
import com.pfplaybackend.api.partyroom.model.entity.PartyroomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

public interface PartyroomRepository extends JpaRepository<PartyroomData, PartyroomId> {
    PartyroomUser findByUserId(String userId);
    String findChatroomIdByUserIdUid(UUID uid);
}
