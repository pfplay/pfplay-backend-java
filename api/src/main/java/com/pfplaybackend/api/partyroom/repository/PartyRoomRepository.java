package com.pfplaybackend.api.partyroom.repository;


import com.pfplaybackend.api.entity.PartyRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PartyRoomRepository extends JpaRepository<PartyRoom, Long> {

    PartyRoom findByUserId(Long userId);
    List<PartyRoom> findByDomain(String domain);

}
