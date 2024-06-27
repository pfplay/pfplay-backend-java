package com.pfplaybackend.api.partyroom.repository;

import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyroomRepository extends JpaRepository<PartyroomData, Long> {
//    PartyroomUser findByUserId(String userId);
//    String findChatroomIdByUserIdUid(UUID uid);
}