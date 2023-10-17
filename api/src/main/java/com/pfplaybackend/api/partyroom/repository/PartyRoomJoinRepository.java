package com.pfplaybackend.api.partyroom.repository;


import com.pfplaybackend.api.entity.PartyRoomJoin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyRoomJoinRepository extends JpaRepository<PartyRoomJoin, Long> {
}
