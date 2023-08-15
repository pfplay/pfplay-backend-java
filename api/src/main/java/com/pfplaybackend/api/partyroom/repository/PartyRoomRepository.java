package com.pfplaybackend.api.partyroom.repository;


import com.pfplaybackend.api.entity.PartyRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface PartyRoomRepository extends JpaRepository<PartyRoom, Long> {

    // @TODO 파티룸을 여러개 만들 수 있는 지 확인 후 리턴값 수정
    Collection<PartyRoom> findByUserId(Long userId);

}
